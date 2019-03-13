import React from 'react'
import OffenderSearchSummary from '../containers/offenderSearchSummaryContainer'
import RestrictedOffenderSearchSummary from '../containers/restrictedOffenderSearchSummaryContainer'
import SearchResultsTitle from '../containers/searchResultsTitleContainer'
import PageSelection from '../containers/pageSelectionContainer'
import OtherAreasFilter from '../containers/otherAreasFilterContainer'
import MyAreasFilter from '../containers/myAreasFilterContainer'
import SearchTypeSelector from '../containers/searchTypeSelectorContainer'

import PropTypes from 'prop-types'

const OffenderSearchResults = ({ results }) => (
  <div id='offender-results' aria-live='polite'>

      <SearchResultsTitle />

      <div className='govuk-grid-row'>
        <div className='govuk-grid-column-one-third'>

          <SearchTypeSelector />
          <MyAreasFilter />
          <OtherAreasFilter />

        </div>
        <div className='govuk-grid-column-two-thirds'>

          <ul id='live-offender-results' className='govuk-list'>
            {results.map(offenderSummary => (
              renderSummary(offenderSummary)
            ))}
          </ul>
          <PageSelection />

        </div>
      </div>
  </div>
)

OffenderSearchResults.propTypes = {
  results: PropTypes.array.isRequired
}

const renderSummary = offenderSummary => {
  if (offenderSummary.accessDenied) {
    return <RestrictedOffenderSearchSummary offenderSummary={offenderSummary} key={offenderSummary.offenderId} />
  }
  return <OffenderSearchSummary offenderSummary={offenderSummary} key={offenderSummary.offenderId} />
}

export default OffenderSearchResults
