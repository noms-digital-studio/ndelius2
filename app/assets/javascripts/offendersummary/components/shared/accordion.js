import React, { Component, Fragment } from 'react';
import * as PropTypes from 'prop-types';

class Accordion extends Component {

    constructor(props) {
        super(props);
        this.state = { isOpen: false };
    }

    render() {

        let accordionID = 'moj-accordion-' + this.props.label.toLowerCase().split(' ').join('-').trim();

        return (
            <Fragment>
                <div id={accordionID} className="moj-accordion">
                    <a className="govuk-link govuk-link--no-visited-state moj-link--no-underline govuk-heading-m govuk-!-margin-0" href="#" role="button"
                       aria-expanded={ this.state.isOpen } onClick={ (e) => {
                            e.preventDefault();
                            this.setState({ isOpen: !this.state.isOpen });
                        } }>{ this.props.label }<span
                        className="qa-accordion-label govuk-heading-xl govuk-!-margin-bottom-0 moj-accordion__toggle" aria-hidden="true"
                        style={ { lineHeight: 0.7 } }>{ this.state.isOpen ? (<Fragment>&ndash;</Fragment>) : '+' }</span>
                    </a>
                    <div className={ 'qa-accordion-content govuk-!-margin-top-4' + (this.state.isOpen ? '' : ' govuk-visually-hidden') } aria-hidden={!this.state.isOpen}>
                        <div className="moj-inside-panel">
                            { this.props.content }
                        </div>
                    </div>
                </div>
                <hr className="govuk-!-margin-0 govuk-!-margin-top-3 govuk-!-margin-bottom-3"/>
            </Fragment>
        );
    }
}

Accordion.propTypes = {
    label: PropTypes.string,
    content: PropTypes.node
};

export default Accordion;