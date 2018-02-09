import {Component} from 'react'
import GovUkPhaseBanner from './govukPhaseBanner';
import PropTypes from 'prop-types'

class FeedbackPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            rating: '',
            feedback: ''
        };
    }

    render() {
        return (<div>
            <div id="root">
                <main id="content">
                    <GovUkPhaseBanner basicVersion={true}/>
                    <div className="key-content">
                        <h1 className="heading-xlarge">Give feedback</h1>
                        <form onSubmit={event => this.submitFeedback(event)}>
                            <fieldset>
                                <legend className="margin-bottom medium">
                                    <span className="form-label-bold">Overall, how did you feel about the National Search service you used today?</span>
                                </legend>

                                <div className="form-group">

                                    <div className="multiple-choice">
                                        <input name="rating" onChange={event => this.handleRatingChange(event)}
                                               type="radio"
                                               id="rating_verysatisfied" value="Very satisfied"/><label
                                        htmlFor="rating_verysatisfied">Very satisfied</label>
                                    </div>
                                    <div className="multiple-choice">
                                        <input name="rating" onChange={event => this.handleRatingChange(event)}
                                               type="radio"
                                               id="rating_satisfied" value="Satisfied"/><label
                                        htmlFor="rating_satisfied">Satisfied</label>
                                    </div>
                                    <div className="multiple-choice">
                                        <input name="rating" onChange={event => this.handleRatingChange(event)}
                                               type="radio"
                                               id="rating_neithersatisfiedordissatisfied"
                                               value="Neither satisfied or dissatisfied"/><label
                                        htmlFor="rating_neithersatisfiedordissatisfied">Neither satisfied or
                                        dissatisfied</label>
                                    </div>
                                    <div className="multiple-choice">
                                        <input name="rating" onChange={event => this.handleRatingChange(event)}
                                               type="radio"
                                               id="rating_dissatisfied" value="Dissatisfied"/><label
                                        htmlFor="rating_dissatisfied">Dissatisfied</label>
                                    </div>
                                    <div className="multiple-choice">
                                        <input name="rating" onChange={event => this.handleRatingChange(event)}
                                               type="radio"
                                               id="rating_verydissatisfied" value="Very dissatisfied"/><label
                                        htmlFor="rating_verydissatisfied">Very dissatisfied</label>
                                    </div>

                                </div>
                                <div className="form-group">
                                    <label htmlFor="feedback">
                                        <span
                                            className="form-label-bold">If you could change anything what would it be?</span>
                                    </label>
                                    <textarea value={this.state.feedback} className="form-control form-control-3-4"
                                              role="textbox" id="feedback" name="feedback" rows="5"
                                              onChange={event => this.handleFeedbackChange(event)}/>
                                </div>
                                <div className="form-group">
                                    <input className="button" type="submit" value="Send feedback"/>
                                </div>
                            </fieldset>
                        </form>
                    </div>
                </main>
            </div>
        </div>)
    }

    handleFeedbackChange(event) {
        this.setState({feedback: event.target.value});
    }

    handleRatingChange(event) {
        this.setState({rating: event.target.value});
    }

    submitFeedback(event) {
        const ratingQuestion = encodeURIComponent('Overall, how did you feel about the National Search service you used today?')
        const feedbackQuestion = encodeURIComponent('If you could change anything what would it be?')
        const emailAddress = 'nick.gallon@digital.justice.gov.uk'
        const subject = encodeURIComponent('National Search Feedback')
        event.preventDefault()
        window.location.href = `mailto:${emailAddress}?subject=${subject}&body=${ratingQuestion}%0D%0A%0D%0A${encodeURIComponent(this.state.rating)}%0D%0A%0D%0A${feedbackQuestion}%0D%0A%0D%0A${encodeURIComponent(this.state.feedback)}`
        this.context.router.goBack()
    }
}

FeedbackPage.contextTypes = {
    router: PropTypes.shape({
        goBack: PropTypes.func.isRequired
    }).isRequired
}
export default FeedbackPage;