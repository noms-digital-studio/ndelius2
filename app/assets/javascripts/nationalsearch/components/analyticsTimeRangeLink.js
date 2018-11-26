import React from 'react'
import PropTypes from 'prop-types'

const AnalyticsTimeRangeLink = ({changeTimeRange, currentTimeRange, timeRange, children}) => (
    <span>
        <a tabIndex={1} href='javascript:' style={{fontWeight: toFontWeight(timeRange, currentTimeRange)}} className="clickable" onClick={() => changeTimeRange(timeRange)}>{children}</a>
    </span>
)

AnalyticsTimeRangeLink.propTypes = {
    changeTimeRange: PropTypes.func.isRequired,
    currentTimeRange: PropTypes.string.isRequired,
    timeRange: PropTypes.string.isRequired,
    children: PropTypes.node.isRequired
}

const toFontWeight = (timeRange, currentTimeRange) => isHighlighted(timeRange, currentTimeRange) ? 'bold' : 'normal'
const isHighlighted = (timeRange, currentTimeRange) => timeRange === currentTimeRange
export default AnalyticsTimeRangeLink
