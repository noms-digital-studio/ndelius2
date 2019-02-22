import React, { Component, Fragment } from 'react'
import * as PropTypes from 'prop-types'
import GovUkPhaseBanner from './govukPhaseBanner'
import ErrorMessage from './errorMessage'
import OffenderIdentity from '../containers/offenderIdentityContainer'
import OffenderCards from '../containers/offenderCardsContainer'
import OffenderDetails from '../containers/offenderDetailsContainer'
import Registrations from '../containers/registrationsContainer'
import SeriousRegistrations from '../containers/seriousRegistrationsContainer'
import Convictions from '../containers/convictionsContainer'
import Notes from '../containers/notesContainer'
import OffenderManager from '../containers/offenderManagerContainer'
import FrameNavigation from '../containers/frameNavigationContainer'

import { configureOffenderSummaryAccordionTracking } from '../../helpers/offenderSummaryAccordionAnalyticsHelper'

class OffenderSummaryPage extends Component {
  componentWillMount () {
    const { getOffenderDetails } = this.props
    getOffenderDetails()
  }

  componentDidUpdate () {
    const { fetching, error, childrenFetching } = this.props
    const $accordion = document.querySelector('[data-module="accordion"]')

    if ($accordion && !fetching && !error && !this.hasRendered && !childrenFetching) {
      this.hasRendered = true
      new window.GOVUKFrontend.Accordion($accordion).init()
      configureOffenderSummaryAccordionTracking()
    }
  }

  render () {
    const { fetching, error } = this.props

    return (
      <Fragment>
        <GovUkPhaseBanner />
        <FrameNavigation />
        {!fetching && !error &&
        <div className='qa-main-content'>

          <OffenderIdentity />
          <OffenderCards />
          <SeriousRegistrations />
          <div className='govuk-accordion' data-module='accordion' id='accordion-offender-summary'>
            <Registrations />
            <Convictions />
            <OffenderManager />
            <OffenderDetails />
          </div>
          <Notes />

        </div>
        }
        {!fetching && error &&
        <ErrorMessage
          message="Unfortunately, we cannot display you the offender's information at the moment. Please try again later." />
        }
      </Fragment>
    )
  }
}

OffenderSummaryPage.propTypes = {
  getOffenderDetails: PropTypes.func,
  fetching: PropTypes.bool,
  error: PropTypes.bool,
  childrenFetching: PropTypes.bool
}

export default OffenderSummaryPage
