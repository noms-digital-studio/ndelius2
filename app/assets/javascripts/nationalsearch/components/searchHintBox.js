import React from 'react'
import PropTypes from 'prop-types'

const SearchHintBox = ({ hint }) => (
  <p className='margin-top margin-bottom large'>
    <span className='search-hint font-medium'>{hint}</span>
  </p>
)

SearchHintBox.propTypes = {
  hint: PropTypes.string
}

export default SearchHintBox
