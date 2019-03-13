import React from 'react'
import PropTypes from 'prop-types'

const searchTypeSelector = ({ searchType, searchTypeChanged, search, searchTerm, probationAreasFilter }) => (

  <div className='govuk-form-group govuk-!-margin-top-4'>
    <fieldset className='govuk-fieldset'>
      <legend className='govuk-fieldset__legend'>Match all terms</legend>
      <div className='govuk-radios govuk-!-margin-top-2 govuk-radios--inline'>
        <div className='govuk-radios__item'>
          <input tabIndex='3' className='govuk-radios__input' type='radio' id='match-all-terms-yes' value='exact'
                 checked={searchType === 'exact'}
                 onChange={
                   event => {
                     search(searchTerm, event.target.value, probationAreasFilter)
                     searchTypeChanged(event.target.value)
                   }
                 } />
          <label className='govuk-label govuk-radios__label' htmlFor='match-all-terms-yes'>Yes</label>
        </div>
        <div className='govuk-radios__item'>
          <input tabIndex='3' className='govuk-radios__input' type='radio' id='match-all-terms-no' value='broad'
                 checked={searchType === 'broad'}
                 onChange={
                   event => {
                     search(searchTerm, event.target.value, probationAreasFilter)
                     searchTypeChanged(event.target.value)
                   }
                 } />
          <label className='govuk-label govuk-radios__label' htmlFor='match-all-terms-no'>No</label>
        </div>
      </div>
    </fieldset>
  </div>
)

searchTypeSelector.propTypes = {
  searchType: PropTypes.string.isRequired,
  searchTypeChanged: PropTypes.func.isRequired,
  search: PropTypes.func.isRequired
}

export default searchTypeSelector
