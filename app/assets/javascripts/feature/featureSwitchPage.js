import React, { Component } from 'react'
import { Cookies, withCookies } from 'react-cookie'
import { instanceOf } from 'prop-types'

class FeatureSwitchPage extends Component {
  constructor (props) {
    super(props)

    const { cookies } = props
    this.state = {
      offenderSummary: cookies.get('featureOffenderSummary') || 'false'
    }
  }

  render () {
    const { offenderSummary } = this.state
    return (
      <div className='govuk-!-margin-left-6'>
        <h1 className='govuk-heading-xl govuk-!-margin-top-6'>Alpha feature switches</h1>
        <div className='govuk-form-group'>
          <fieldset className='govuk-fieldset'>
            <legend className='govuk-fieldset__legend govuk-label'>Offender summary</legend>
            <div className='govuk-radios govuk-!-margin-top-2 govuk-radios--inline'>
              <div className='govuk-radios__item'>
                <input
                  className='govuk-radios__input'
                  type='radio'
                  id='offender-summary-true'
                  value='true'
                  checked={offenderSummary === 'true'}
                  onChange={() => this.offenderSummary('true')} />
                <label className='govuk-label govuk-radios__label' htmlFor='offender-summary-true'> Yes </label>
              </div>
              <div className='govuk-radios__item'>
                <input
                  className='govuk-radios__input'
                  type='radio' id='offender-summary-false'
                  value='false'
                  checked={offenderSummary === 'false'}
                  onChange={() => this.offenderSummary('false')} />
                <label className='govuk-label govuk-radios__label' htmlFor='offender-summary-false'> No </label>
              </div>
            </div>
          </fieldset>
        </div>
      </div>)
  }

  offenderSummary (enabled) {
    const { cookies } = this.props
    cookies.set('featureOffenderSummary', enabled)
    this.setState({ offenderSummary: enabled })
  }
}

FeatureSwitchPage.propTypes = {
  cookies: instanceOf(Cookies).isRequired
}

export default withCookies(FeatureSwitchPage)
