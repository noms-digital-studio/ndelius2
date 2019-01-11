import React, {Fragment} from 'react'
import * as PropTypes from 'prop-types'
import ErrorMessage from './errorMessage'
import Accordion from './shared/accordion'
import NextAppointment from '../containers/nextAppointmentContainer'

const OffenderManager = ({ fetching, error }) => {
    return(
        <Accordion label='Offender manager'>
            <Fragment>
                {!fetching && !error &&
                <div className="moj-inside-panel qa-offender-manager">
                    <NextAppointment/>
                </div>
                }
                {!fetching && error &&
                <ErrorMessage
                    message="Unfortunately, we cannot display you the offender's registrations at the moment. Please try again later."/>
                }

            </Fragment>
        </Accordion>
    )
}


OffenderManager.propTypes = {
    fetching: PropTypes.bool,
    error: PropTypes.bool
}

export default OffenderManager