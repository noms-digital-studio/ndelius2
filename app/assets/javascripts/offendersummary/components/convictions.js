import React, { Component, Fragment } from 'react'
import * as PropTypes from 'prop-types'
import Accordion from './shared/accordion'
import ErrorMessage from './errorMessage'
import { dateFromISO } from '../../helpers/formatters'
import { convictionDescription, convictionSorter, mainOffenceDescription } from '../../helpers/convictionsHelper'

class Convictions extends Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() {
        const { getOffenderConvictions } = this.props;
        getOffenderConvictions();
    }

    render() {
        const {fetching, error, convictions, maxConvictionsVisible, showMoreConvictions} = this.props;

        return (
            <Accordion label={`Events (${convictions.length})`}>
                <Fragment>
                    {!fetching && !error &&
                    <div className="moj-inside-panel qa-offender-convictions">
                        {convictions.length === 0 &&
                        <div><p className="govuk-body moj-!-text-align-center">No events recorded</p></div>
                        }
                        {convictions.length > 0 &&
                        <table className="govuk-table moj-table moj-table--split-rows govuk-!-margin-0" role="presentation">
                            <tbody>
                            {convictions.sort(convictionSorter).slice(0, maxConvictionsVisible).map(renderConviction)}
                            </tbody>
                        </table>
                        }

                        {convictions.length > maxConvictionsVisible &&
                        <div className="moj-timeline__item moj-timeline__item--more-items govuk-!-margin-top-4">
                            <p className="govuk-body moj-!-text-align-center govuk-!-margin-bottom-0">
                                <a href='javascript:' onClick={showMoreConvictions}>Show more events</a>
                            </p>
                        </div>
                        }
                    </div>
                    }
                    {!fetching && error &&
                    <ErrorMessage
                        message="Unfortunately, we cannot display you the offender's events at the moment. Please try again later."/>
                    }

                </Fragment>
            </Accordion>
        )
    }
}

const renderConviction = conviction => {
    return (
        <Fragment key={conviction.convictionId}>
        <tr>
            <td className="moj-!-border-0 govuk-!-padding-bottom-0" colSpan="3">
                <p className="govuk-!-margin-0 govuk-heading-s moj-!-color-blue moj-util-clickable">{convictionDescription(conviction)}</p>
            </td>
        </tr>
        <tr>
            <td className="govuk-!-padding-top-0"><p
                className="govuk-body moj-!-color-grey govuk-!-margin-bottom-0">{mainOffenceDescription(conviction)}</p></td>
            <td className="govuk-!-padding-top-0" width="110"><p
                className="govuk-body moj-!-color-grey govuk-!-margin-bottom-0">{dateFromISO(conviction.referralDate)}</p></td>
            <td className="govuk-!-padding-top-0" width="100"><p
                className={`govuk-body govuk-!-font-size-19 moj-!-text-align-right govuk-!-margin-bottom-0 moj-!-color-${conviction.active? 'green' : 'red'}`}>{conviction.active? 'Active' : 'Terminated'}</p>
            </td>
        </tr>
        </Fragment>
    );
}

Convictions.propTypes = {
    getOffenderConvictions: PropTypes.func,
    showMoreConvictions: PropTypes.func,
    fetching: PropTypes.bool,
    error: PropTypes.bool,
    maxConvictionsVisible: PropTypes.number.isRequired,
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