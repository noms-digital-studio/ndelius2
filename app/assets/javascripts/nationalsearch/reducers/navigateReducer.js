import {
    ADD_CONTACT,
    LEGACY_SEARCH,
    SHOW_OFFENDER_DETAILS,
    ADD_NEW_OFFENDER
} from '../actions/navigate'

const navigate = (state = {shouldClose: false}, action) => {
    switch (action.type) {
        case ADD_CONTACT:
            return {shouldClose: true, action: 'addContact', data: action.offenderId};
        case LEGACY_SEARCH:
            return {shouldClose: true, action: 'toggleSearch'};
        case SHOW_OFFENDER_DETAILS:
            return {shouldClose: true, action: 'viewOffender', data: action.offenderId};
        case ADD_NEW_OFFENDER:
            return {shouldClose: true, action: 'addOffender'};
        default:
            return state
    }
};

export default navigate

