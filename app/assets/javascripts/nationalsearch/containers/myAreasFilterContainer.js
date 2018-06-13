import { connect } from 'react-redux'
import areaFilter from '../components/filter'
import {addAreaFilter, removeAreaFilter, search} from '../actions/search'
import {alphabeticalOnProperty, sort} from '../../helpers/streams'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm,
        filterValues: extractMyProbationAreas(state.search.byProbationArea, state.search.myProbationAreas),
        currentFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter),
        name: 'my-providers',
        title: 'My providers',
        searchType: state.search.searchType
    }),
    dispatch => ({
        addToFilter: (probationAreaCode, probationAreaDescription)  => dispatch(addAreaFilter(probationAreaCode, probationAreaDescription)),
        removeFromFilter: probationAreaCode => dispatch(removeAreaFilter(probationAreaCode)),
        search: (searchTerm, searchType, probationAreasFilter) =>
            dispatch(search(searchTerm, searchType, probationAreasFilter))
    })
)(areaFilter)


export const extractMyProbationAreas = (byProbationArea, myProbationAreas) => {
    const find = (myAreaCode) => byProbationArea.filter(area => area.code === myAreaCode).shift()

    const create = (myAreaCode) => ({code: myAreaCode, description: myProbationAreas[myAreaCode], count: 0})

    return sort(Object.getOwnPropertyNames(myProbationAreas)
        .map(myAreaCode => find(myAreaCode) || create(myAreaCode)), alphabeticalOnProperty('description'))
}

