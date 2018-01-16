import PropTypes from 'prop-types'

const LegacySearchLink = ({legacySearch, children}) => (
    <a className="clickable" onClick={legacySearch}>{children}</a>
)

LegacySearchLink.propTypes = {
    legacySearch: PropTypes.func.isRequired,
    children: PropTypes.node.isRequired
}


export default LegacySearchLink
