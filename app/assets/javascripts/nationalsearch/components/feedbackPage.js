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
                                <div className="form-group">
                                    <span className="form-label-bold">Overall, how did you feel about the National Search service you used today?</span>
                                </div>
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
                                        htmlFor="rating_neithersatisfiedordissatisfied">Neither satisfied or dissatisfied</label>
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
                                    <label className="form-label-bold" htmlFor="email">Email</label>
                                    <p>We&apos;ll use this to respond to you directly.</p>
                                    <input className="form-control" id="email" type='email'
                                           onChange={event => this.handleEmailChange(event)}/>
                                </div>
                                <div className="form-group">
                                    <label className="form-label" htmlFor="role">What is your role?</label>
                                    <select className="form-control" id="role" name="role"
                                            onChange={event => this.handleRoleChange(event)}>
                                        <option>&nbsp;</option>
                                        <option>Probation Officer</option>
                                        <option>Senior Probation Officer</option>
                                        <option>Case Administrator</option>
                                        <option>Offender Manager in the Community</option>
                                        <option>Offender Manager in Prison</option>
                                        <option>Court Duty Officer</option>
                                        <option>Other</option>
                                    </select>
                                </div>
                                {this.state.showOtherRole &&
                                    <div className="form-group panel panel-border-narrow">
                                        <label className="form-label" htmlFor="role-other">Please specify your role</label>
                                        <input className="form-control" id="role-other"
                                               onChange={event => this.handleRoleOtherChanged(event)}/>
                                    </div>
                                }
                                <div className="form-group">
                                    <label className="form-label" htmlFor="provider">Who do you work for?</label>
                                    <select className="form-control" id="provider" name="provider"
                                            onChange={event => this.handleProviderChange(event)}>
                                        <option>&nbsp;</option>
                                        <option>NPS</option>
                                        <option>CRC</option>
                                        <option>Other</option>
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label className="form-label" htmlFor="region">Which region do you work in?</label>
                                    <select className="form-control" id="region" name="region"
                                            onChange={event => this.handleRegionChange(event)}>
                                        <option>&nbsp;</option>
                                        <option>North East</option>
                                        <option>North West</option>
                                        <option>Wales</option>
                                        <option>Midlands</option>
                                        <option>South West & South Central</option>
                                        <option>South East</option>
                                        <option>London</option>
                                    </select>
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

    handleEmailChange(event) {
        this.setState({email: event.target.value});
    }

    handleRoleChange(event) {
        this.setState({role: event.target.value});
        this.setState({showOtherRole: event.target.value === 'Other'})
    }

    handleRoleOtherChanged(event) {
        this.setState({roleOther: event.target.value});
    }

    handleProviderChange(event) {
        this.setState({provider: event.target.value});
    }

    handleRegionChange(event) {
        this.setState({region: event.target.value});
    }

    submitFeedback(event) {
        const {addFeedback} = this.props
        event.preventDefault()
        if (this.state.role === 'Other' && this.state.roleOther !== undefined) {
            this.state.role = this.state.roleOther
        } else if (this.state.role === 'Other' && this.state.roleOther === undefined) {
            this.state.role = 'Other'
        }
        addFeedback({
            email: this.state.email,
            rating: this.state.rating,
            feedback: this.state.feedback,
            role: this.state.role,
            provider: this.state.provider,
            region: this.state.region
        }, this.context.router)
    }
}

FeedbackPage.contextTypes = {
    router: PropTypes.shape({
        goBack: PropTypes.func.isRequired
    }).isRequired
}
export default FeedbackPage;