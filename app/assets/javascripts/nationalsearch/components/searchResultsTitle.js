import React from 'react'
import PropTypes from 'prop-types'

const SearchResultsTitle = ({ pageNumber, pageSize, total, resultsReceived }) => {
  if (resultsReceived === false) {
    return (<div />)
  }

  return (
    <h2 aria-live='polite' className='heading-medium margin-top medium search-results-title'>
      {renderHeader(pageNumber, pageSize, total)}
    </h2>
  )
}

const renderHeader = (pageNumber, pageSize, total) => {
  if (total === 0) {
    return (<span>0 results found</span>)
  }
  const resultPlural = total === 1 ? 'result' : 'results'
  if (numberOfPages(pageSize, total) === 1) {
    return (<span>{`${formatNumber(total)} ${resultPlural} found`}</span>)
  }
  return (
    <span>{`${formatNumber(total)} ${resultPlural} found, showing ${formatNumber(fromResult(pageNumber, pageSize))} to ${formatNumber(toResult(pageNumber, pageSize, total))}`}</span>
  )
}

const fromResult = (pageNumber, pageSize) => ((pageNumber - 1) * pageSize) + 1
const toResult = (pageNumber, pageSize, total) => Math.min(total, pageNumber * pageSize)
const numberOfPages = (pageSize, total) => Math.ceil(total / pageSize)
const formatNumber = number => number.toLocaleString().replace('00', '')
SearchResultsTitle.propTypes = {
  pageNumber: PropTypes.number.isRequired,
  pageSize: PropTypes.number.isRequired,
  total: PropTypes.number.isRequired,
  resultsReceived: PropTypes.bool.isRequired
}

export default SearchResultsTitle
