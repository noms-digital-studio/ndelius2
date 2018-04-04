import {CLEAR_RESULTS, REQUEST_SEARCH, SEARCH_RESULTS, PAGE_SIZE, NO_SAVED_SEARCH} from '../actions/search'
import {flatMap} from '../../helpers/streams'

const searchResults = (state = {searchTerm: '', resultsSearchTerm: '', resultsReceived: false, results: [], suggestions: [], total: 0, pageNumber: 1, firstTimeIn: true, showWelcomeBanner: false}, action) => {
    switch (action.type) {
        case REQUEST_SEARCH:
            return {
                ...state,
                searchTerm: action.searchTerm
            };
        case SEARCH_RESULTS:
            if (areSearchResultsStillRelevant(state, action)) {
                return {
                    searchTerm: state.searchTerm,
                    resultsSearchTerm: action.searchTerm,
                    pageNumber: action.pageNumber,
                    total: action.results.total,
                    results: mapResults(action.results.offenders, state.searchTerm, action.pageNumber),
                    suggestions: mapSuggestions(action.results.suggestions),
                    resultsReceived: true,
                    firstTimeIn: false,
                    showWelcomeBanner: false
                };
            }
            return state
        case CLEAR_RESULTS:
            return {
                ...state,
                searchTerm: '',
                resultsSearchTerm: '',
                results: [],
                suggestions: [],
                total: 0,
                pageNumber: 1,
                resultsReceived: false
            };
        case NO_SAVED_SEARCH:
            return {
                ...state,
                showWelcomeBanner: state.firstTimeIn
            }
        default:
            return state
    }
};

export default searchResults

const mapResults = (results = [], searchTerm, pageNumber) =>
    results.map(
        (offenderDetails, index) => ({
            ...offenderDetails,
            rankIndex: toRankIndex(index, pageNumber),
            aliases: offenderAliasesWithUnwantedFieldsRemoved(offenderDetails).map((alias) => {
                return {
                    ...alias
                }
            }),
            addresses: offenderAddressesWithUnwantedFieldsRemoved(offenderDetails).map((address) => {
                return {
                    ...address
                }
            })}
        )
    )

const toRankIndex = (index, pageNumber) => ((pageNumber - 1) * PAGE_SIZE) + index + 1
const offenderAliasesWithUnwantedFieldsRemoved = (offenderDetails) => {
    const aliases = offenderAliases(offenderDetails);
    aliases.forEach(alias => removeFields(alias, ['dateOfBirth', 'gender']))
    return aliases
}
const offenderAliases = (offenderDetails) => offenderDetails.offenderAliases || []

const offenderAddressesWithUnwantedFieldsRemoved = (offenderDetails) => {
    const addresses = offenderAddresses(offenderDetails);
    addresses.forEach(address => removeFields(address, ['from']))
    return addresses
}
const offenderAddresses = (offenderDetails) => offenderContactDetails(offenderDetails).addresses || []
const offenderContactDetails = (offenderDetails) => offenderDetails.contactDetails || {}

const mapSuggestions = suggestions => {
    if (suggestions && suggestions.suggest && Object.getOwnPropertyNames(suggestions.suggest).length > 0) {
        return flatMap(Object.getOwnPropertyNames(suggestions.suggest), suggestField => suggestions.suggest[suggestField])
            .filter(searchedWordsWithSuggestions => searchedWordsWithSuggestions.options.length > 0)
    }

    return []
}

const removeFields = (object, fieldsToRemove) => {
    fieldsToRemove.forEach(field => delete object[field])
    return object
}

const areSearchResultsStillRelevant = (state, action) => state.searchTerm.indexOf(action.searchTerm) > -1