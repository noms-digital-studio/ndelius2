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
import { nodeListForEach } from 'govuk-frontend/common'

class OffenderSummaryPage extends Component {

  constructor (props) {
    super(props)

    this.state = {
      hasRendered: false
    }
  }

  componentWillMount () {
    const {getOffenderDetails} = this.props
    getOffenderDetails()
  }

  render () {
    const {fetching, error} = this.props

    if (!fetching && !error) {
      setTimeout(() => {
        const $accordions = document.querySelectorAll('[data-module="accordion"]')
        if ($accordions) {
          nodeListForEach($accordions, ($accordion) => {
            new window.GOVUKFrontend.Accordion($accordion).init()
          })
        }
      })
    }

    return (
      <Fragment>
        <GovUkPhaseBanner/>
        { !fetching && !error &&
        <div className="qa-main-content">

          <OffenderIdentity/>
          <OffenderCards/>
          <SeriousRegistrations/>
          <div className="govuk-accordion" data-module="accordion" id="accordion-offender-summary">
            <Registrations/>
            <Convictions/>
            <OffenderManager/>
            <OffenderDetails/>
          </div>
          <Notes/>

        </div>
        }
        { !fetching && error &&
        <ErrorMessage
          message="Unfortunately, we cannot display you the offender's information at the moment. Please try again later."/>
        }
      </Fragment>
    )
  }
}

OffenderSummaryPage.propTypes = {
  getOffenderDetails: PropTypes.func,
  fetching: PropTypes.bool,
  error: PropTypes.bool
}

export default OffenderSummaryPage
