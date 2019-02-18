import React from 'react'
import PropTypes from 'prop-types'

const LegacySearchLink = ({legacySearch, children, tabIndex}) => (
    <a className="clickable" href='javascript:' title="Use the previous search" tabIndex={tabIndex} onClick={legacySearch}>{children}</a>
)

LegacySearchLink.propTypes = {
    legacySearch: PropTypes.func.isRequired,
    children: PropTypes.node.isRequired,
    tabIndex: PropTypes.string
}


export default LegacySearchLink
