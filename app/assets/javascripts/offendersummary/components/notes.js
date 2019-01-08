import React, {Fragment} from 'react'
import * as PropTypes from 'prop-types'
import ErrorMessage from './errorMessage'

const Notes = ({ fetching, error, notes }) => {
    return(
        <Fragment>
            {!fetching && !error &&
            <div className="moj-card govuk-!-margin-top-4 qa-offender-notes">
                <h2 className="govuk-heading-m govuk-!-margin-bottom-2">Notes</h2>
                <div className="moj-card__body moj-card__body--no-border govuk-!-padding-4">
                    {notes.split('\n').map(renderNote)}
                </div>
            </div>

            }
            {!fetching && error &&
            <ErrorMessage
                message="Unfortunately, we cannot display you the offender's notes at the moment. Please try again later."/>
            }

        </Fragment>
    )
}

const renderNote = (note, index) => {
    return (<p key={index} className="govuk-body govuk-!-margin-0">{note}</p>)
}

Notes.propTypes = {
    fetching: PropTypes.bool,
    error: PropTypes.bool,
    notes: PropTypes.string
}

export default Notes