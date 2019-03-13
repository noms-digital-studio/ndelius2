import React from 'react'
import PropTypes from 'prop-types'

const SearchHintBox = ({ hint }) => (
  <p>
    <span className='app-search-hint govuk-body-m govuk-!-font-weight-bold'>{hint}</span>
  </p>
)

SearchHintBox.propTypes = {
  hint: PropTypes.string
}

export default SearchHintBox
