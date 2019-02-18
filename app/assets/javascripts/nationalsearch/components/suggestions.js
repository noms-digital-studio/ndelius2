import React from 'react'
import PropTypes from "prop-types";
import {flatMap} from '../../helpers/streams'

const Suggestions = ({searchTerm, searchType, probationAreasFilter, suggestions, search}) => {
    if (suggestions.length === 0) {
        return (<span/>);
    }
    return (
        <p className='margin-top medium no-margin-bottom'>
            <span>Did you mean</span>
            {suggestionsToOrderedMapping(suggestions).map( (suggestion, index) =>
                <span key={index}>
                    &nbsp;
                    <a className='white' tabIndex="1" href='javascript:'
                       title={`Search again using ${suggestion.text}`}
                       onClick={() => search(replace(searchTerm, suggestion.original, suggestion.text), searchType, probationAreasFilter)}>
                     {suggestion.text}
                     </a>
                </span>)}
            <span>?</span>
        </p>
    )

}

const suggestionsToOrderedMapping = suggestions =>
    sortByScore(flatMap(suggestions, (
        suggestion =>
            suggestion.options.map(option => ({
                                text: option.text,
                                score: option.score,
                                original: suggestion.text})
    )))).slice(0, 4)
const replace = (searchTerm, from, to) => searchTerm.replace(new RegExp(from, 'gi'), to)
const sortByScore = (array) => {
    array.sort(scoreComparator)
    return array
}
const scoreComparator = (a, b) => b.score - a.score

Suggestions.propTypes = {
    suggestions: PropTypes.arrayOf(
        PropTypes.shape({
            text: PropTypes.string.isRequired,
            options: PropTypes.arrayOf(
                PropTypes.shape({
                    text: PropTypes.string.isRequired,
                    score: PropTypes.number.isRequired,
                })
            )
        })
    ),
    searchTerm: PropTypes.string.isRequired,
    probationAreasFilter: PropTypes.arrayOf(PropTypes.string).isRequired,
    search: PropTypes.func.isRequired,
    searchType: PropTypes.string.isRequired
};


export default Suggestions;
