import PropTypes from "prop-types";
import {flatMap} from '../../helpers/streams'
import moment from 'moment'

const MarkableText = ({text, searchTerm, isDate, highlight, highlightFieldName}) => {
    if (matchesHighlightedField(highlight, highlightFieldName)) {
        const chunks = toHighlightList(text, searchTerm, isDate);
        return (
            <span>
            {chunks.map( (chunk, index) => {
                const fragment = text.substr(chunk.start, chunk.end - chunk.start)
                return (<Text key={index} text={fragment} highlight={chunk.highlight}/>)
            })}
        </span>
        )
    }
    return <span>{text}</span>
}

const Text = ({text, highlight}) =>  {
    if (highlight) {
        return (
            <span className='mark'>{text}</span>
        )
    } else {
        return (
            <span>{text}</span>
        )
    }
}

export const matchesHighlightedField = (highlight, highlightFieldName) => !highlight || Object.getOwnPropertyNames(highlight).indexOf(highlightFieldName) > -1
export const matchesAnyHighlightedField = (highlight, highlightFieldNames) => highlightFieldNames.reduce((accumulator, currentValue) => accumulator || matchesHighlightedField(highlight, currentValue), false)

const toHighlightList = (text, searchTerm, isDate) => findAll({
    autoEscape: true,
    searchWords: isDate ? expandDateFormats(searchTerm.split(' ')) : searchTerm.split(' '),
    textToHighlight: text
})


const expandDateFormats = searchWords => flatMap(searchWords, searchWord => isDate(searchWord) ? expandDateWithAllFormats(searchWord): [searchWord])
const isDate = searchWord => toDateFormat(searchWord)
const toDateFormat = date => supportedDateFormats
    .filter(format => moment(date, format, true).isValid())
    .reduce((accumulator, currentValue) => accumulator || currentValue, '')
const expandDateWithAllFormats = (searchWord) => {
    const date = moment(searchWord, toDateFormat(searchWord))
    return supportedDateFormats.map(format => date.format(format))
}
const supportedDateFormats = ['YYYY/MMMM/DD', 'YYYY-MMMM-DD',
                              'YYYY/MMMM/D', 'YYYY-MMMM-D',
                              'YYYY/MMM/DD', 'YYYY-MMM-DD',
                              'YYYY/MMM/D', 'YYYY-MMM-D',
                              'YYYY/MM/DD', 'YYYY-MM-DD',
                              'YYYY/MM/D', 'YYYY-MM-D',
                              'YYYY/M/DD', 'YYYY-M-DD',
                              'YYYY/M/D', 'YYYY-M-D',
                              'DD/MMMM/YYYY', 'DD-MMMM-YYYY',
                              'D/MMMM/YYYY', 'D-MMMM-YYYY',
                              'DD/MMM/YYYY', 'DD-MMM-YYYY',
                              'D/MMM/YYYY', 'D-MMM-YYYY',
                              'DD/MM/YYYY', 'DD-MM-YYYY',
                              'D/MM/YYYY', 'D-MM-YYYY',
                              'DD/M/YYYY', 'DD-M-YYYY',
                              'D/M/YYYY', 'D-M-YYYY',
                              'DD/MM/YY', 'DD-MM-YY',
                              'DD/M/YY', 'DD-M-YY']
MarkableText.propTypes = {
    text: PropTypes.node.isRequired,
    searchTerm: PropTypes.string.isRequired,
    highlight: PropTypes.object.isRequired,
    highlightFieldName: PropTypes.string.isRequired,
    isDate: PropTypes.bool
};

/**
 * Functions below taken directly from https://github.com/bvaughn/highlight-words-core
 *
 * We currently can't import highlight-words-core due to Webpack version used no longer supports IE8
 * Once we drop IE we should import the library directly.
 *
 * Licence is here https://github.com/bvaughn/highlight-words-core/blob/master/LICENSE
 */

/**
 * Creates an array of chunk objects representing both higlightable and non highlightable pieces of text that match each search word.
 * @return Array of "chunks" (where a Chunk is { start:number, end:number, highlight:boolean })
 */
const findAll = ({
                            autoEscape,
                            caseSensitive = false,
                            searchWords,
                            textToHighlight
                        }) => (
    fillInChunks({
        chunksToHighlight: combineChunks({
            chunks: findChunks({
                autoEscape,
                caseSensitive,
                searchWords,
                textToHighlight
            })
        }),
        totalLength: textToHighlight ? textToHighlight.length : 0
    })
)

/**
 * Takes an array of {start:number, end:number} objects and combines chunks that overlap into single chunks.
 * @return {start:number, end:number}[]
 */
const combineChunks = ({
                                  chunks
                              }) => {
    chunks = chunks
        .sort((first, second) => first.start - second.start)
        .reduce((processedChunks, nextChunk) => {
            // First chunk just goes straight in the array...
            if (processedChunks.length === 0) {
                return [nextChunk]
            } else {
                // ... subsequent chunks get checked to see if they overlap...
                const prevChunk = processedChunks.pop()
                if (nextChunk.start <= prevChunk.end) {
                    // It may be the case that prevChunk completely surrounds nextChunk, so take the
                    // largest of the end indeces.
                    const endIndex = Math.max(prevChunk.end, nextChunk.end)
                    processedChunks.push({start: prevChunk.start, end: endIndex})
                } else {
                    processedChunks.push(prevChunk, nextChunk)
                }
                return processedChunks
            }
        }, [])

    return chunks
}

/**
 * Examine text for any matches.
 * If we find matches, add them to the returned array as a "chunk" object ({start:number, end:number}).
 * @return {start:number, end:number}[]
 */
const findChunks = ({
                               autoEscape,
                               caseSensitive,
                               searchWords,
                               textToHighlight
                           }) => {
    return searchWords
        .filter(searchWord => searchWord) // Remove empty words
        .reduce((chunks, searchWord) => {

            if (autoEscape) {
                searchWord = escapeRegExpFn(searchWord)
            }

            const regex = new RegExp(searchWord, caseSensitive ? 'g' : 'gi')

            let match
            while ((match = regex.exec(textToHighlight))) {
                let start = match.index
                let end = regex.lastIndex
                // We do not return zero-length matches
                if (end > start) {
                    chunks.push({start, end})
                }

                // Prevent browsers like Firefox from getting stuck in an infinite loop
                // See http://www.regexguru.com/2008/04/watch-out-for-zero-length-matches/
                if (match.index == regex.lastIndex) {
                    regex.lastIndex++
                }
            }

            return chunks
        }, [])
}

/**
 * Given a set of chunks to highlight, create an additional set of chunks
 * to represent the bits of text between the highlighted text.
 * @param chunksToHighlight {start:number, end:number}[]
 * @param totalLength number
 * @return {start:number, end:number, highlight:boolean}[]
 */
const fillInChunks = ({
                                 chunksToHighlight,
                                 totalLength
                             }) => {
    const allChunks = []
    const append = (start, end, highlight) => {
        if (end - start > 0) {
            allChunks.push({
                start,
                end,
                highlight
            })
        }
    }

    if (chunksToHighlight.length === 0) {
        append(0, totalLength, false)
    } else {
        let lastIndex = 0
        chunksToHighlight.forEach((chunk) => {
            append(lastIndex, chunk.start, false)
            append(chunk.start, chunk.end, true)
            lastIndex = chunk.end
        })
        append(lastIndex, totalLength, false)
    }
    return allChunks
}

function escapeRegExpFn (str) {
    return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, '\\$&')
}

export default MarkableText;