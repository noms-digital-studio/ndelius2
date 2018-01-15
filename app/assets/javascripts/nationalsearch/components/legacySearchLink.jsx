import PropTypes from 'prop-types'

const LegacySearchLink = ({legacySearch}) => (
    <a className="clickable" onClick={legacySearch}>Advanced search</a>
)

LegacySearchLink.propTypes = {
    legacySearch: PropTypes.func.isRequired
}


export default LegacySearchLink
