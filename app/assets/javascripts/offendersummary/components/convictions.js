import React, { Component, Fragment } from 'react'
import * as PropTypes from 'prop-types'
import Accordion from './shared/accordion'
import ErrorMessage from './errorMessage'
import { dateFromISO } from '../../helpers/formatters'
import { convictionDescription, convictionSorter, mainOffenceDescription } from '../../helpers/convictionsHelper'

class Convictions extends Component {
  componentWillMount () {
    const { getOffenderConvictions } = this.props
    getOffenderConvictions()
  }

  render () {
    const { fetching, error, convictions, maxConvictionsVisible, showMoreConvictions, offenderId, viewOffenderEvent } = this.props
    const renderConviction = conviction => {
      const colorClass = () => conviction.active ? conviction.inBreach ? 'moj-!-color-red' : 'moj-!-color-green' : ''
      const status = () => conviction.active ? conviction.inBreach ? 'Breached' : 'Active' : 'Terminated'

      return (
        <Fragment key={conviction.convictionId}>
          <tr>
            <th className='moj-!-border-0 govuk-!-padding-bottom-0' colSpan='3'>
              <a className='govuk-!-margin-0 govuk-heading-s govuk-link govuk-link--no-visited-state'
                 href='javascript:void(0);'
                 onClick={() => viewOffenderEvent(offenderId, conviction.convictionId)}>{convictionDescription(conviction)}</a>
            </th>
          </tr>
          <tr>
            <td className='govuk-!-padding-top-0'><p
              className='govuk-body moj-!-color-grey govuk-!-margin-bottom-0'>{mainOffenceDescription(conviction)}</p>
            </td>
            <td className='govuk-!-padding-top-0' style={{ width: '110px' }}><p
              className='govuk-body moj-!-color-grey govuk-!-margin-bottom-0'>{dateFromISO(conviction.referralDate)}</p>
            </td>
            <td className='govuk-!-padding-top-0' style={{ width: '100px' }}><p
              className={`govuk-body govuk-!-font-size-19 moj-!-text-align-right govuk-!-margin-bottom-0 ${colorClass()}`}>{status()}</p>
            </td>
          </tr>
        </Fragment>
      )
    }

    return (
      <Accordion label={`Events (${convictions.length})`} id='2'>
        <Fragment>
          {!fetching && !error &&
          <div className='moj-inside-panel qa-offender-convictions'>
            {convictions.length === 0 &&
            <div><p className='govuk-body moj-!-text-align-center'>No events recorded</p></div>
            }
            {convictions.length > 0 &&
            <table className='govuk-table moj-table moj-table--split-rows govuk-!-margin-0'>
              <tbody>
              {convictions.sort(convictionSorter).slice(0, maxConvictionsVisible).map(renderConviction)}
              </tbody>
            </table>
            }

            {convictions.length > maxConvictionsVisible &&
            <div className='moj-timeline__item moj-timeline__item--more-items govuk-!-margin-top-4'>
              <p className='govuk-body moj-!-text-align-center govuk-!-margin-bottom-0'>
                <a href='javascript:' onClick={showMoreConvictions} className='govuk-link govuk-link--no-visited-state'>View more events</a>
              </p>
            </div>
            }
          </div>
          }
          {!fetching && error &&
          <ErrorMessage
            message="Unfortunately, we cannot display you the offender's events at the moment. Please try again later." />
          }

        </Fragment>
      </Accordion>
    )
  }
}

Convictions.propTypes = {
  getOffenderConvictions: PropTypes.func,
  showMoreConvictions: PropTypes.func,
  viewOffenderEvent: PropTypes.func,
  fetching: PropTypes.bool,
  error: PropTypes.bool,
  maxConvictionsVisible: PropTypes.number.isRequired,
  offenderId: PropTypes.number.isRequired,
  convictions: PropTypes.arrayOf(
    PropTypes.shape({
        offences: PropTypes.arrayOf(
          PropTypes.shape({
            mainOffence: PropTypes.bool.isRequired,
            detail: PropTypes.shape(
              {
                description: PropTypes.string.isRequired
              }
            ).isRequired
          }).isRequired
        ).isRequired,
        referralDate: PropTypes.string.isRequired,
        active: PropTypes.bool.isRequired,
        inBreach: PropTypes.bool.isRequired,
        convictionId: PropTypes.number.isRequired,
        sentence: PropTypes.shape(
          {
            description: PropTypes.string.isRequired,
            originalLengthUnits: PropTypes.string.isRequired,
            originalLength: PropTypes.number.isRequired
          }
        ),
        latestCourtAppearanceOutcome: PropTypes.shape(
          {
            description: PropTypes.string.isRequired
          }
        )

      }.isRequired
    ))

}

export default Convictions
