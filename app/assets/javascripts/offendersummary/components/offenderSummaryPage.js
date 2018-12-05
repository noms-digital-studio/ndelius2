import React, { Component } from 'react'
import * as PropTypes from 'prop-types'
import GovUkPhaseBanner from './govukPhaseBanner'
import OffenderIdentity from '../containers/offenderIdentityContainer'


class OffenderSummaryPage extends Component {
    constructor(props) {
        super(props)
    }

    componentWillMount() {
        const {getOffenderDetails} = this.props
        getOffenderDetails()
    }

    render() {
        const {fetching} = this.props
        return(
            <div>
                <GovUkPhaseBanner/>
                {!fetching &&
                <div className="govuk-grid-row govuk-!-margin-top-3">
                    <div className="govuk-grid-column-one-half">
                        <OffenderIdentity/>
                    </div>
                    <div className="govuk-grid-column-one-half">&nbsp;</div>
                </div>
                }
            </div>
        )
    }
}



OffenderSummaryPage.propTypes = {
    getOffenderDetails : PropTypes.func,
    fetching : PropTypes.bool
}

export default OffenderSummaryPage;