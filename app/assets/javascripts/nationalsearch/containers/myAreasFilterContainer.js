import { connect } from 'react-redux'
import areaFilter from '../components/filter'
import {addAreaFilter, removeAreaFilter, search} from '../actions/search'

export default connect(
    state => ({
        searchTerm: state.search.searchTerm,
        filterValues: extractMyProbationAreas(state.search.byProbationArea, state.search.myProbationAreas),
        currentFilter: Object.getOwnPropertyNames(state.search.probationAreasFilter),
        name: 'my-providers',
        title: 'My providers'
    }),
    dispatch => ({
        addToFilter: (probationAreaCode, probationAreaDescription)  => dispatch(addAreaFilter(probationAreaCode, probationAreaDescription)),
        removeFromFilter: probationAreaCode => dispatch(removeAreaFilter(probationAreaCode)),
        search: (searchTerm, probationAreasFilter) => dispatch(search(searchTerm, probationAreasFilter))
    })
)(areaFilter)


export const extractMyProbationAreas = (byProbationArea, myProbationAreas) => {
    const find = (myAreaCode) => byProbationArea.filter(area => area.code === myAreaCode).shift()

    const create = (myAreaCode) => ({code: myAreaCode, description: myProbationAreas[myAreaCode], count: 0})

    return Object.getOwnPropertyNames(myProbationAreas)
        .map(myAreaCode => find(myAreaCode) || create(myAreaCode))
}

