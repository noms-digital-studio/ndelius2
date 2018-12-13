import { Fragment } from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';
import Accordion from './accordion';

describe('Accordion component (shared)', () => {

    let wrapper;

    describe('Accordion', () => {

        beforeEach(() => {
            wrapper = shallow(<Accordion label="Offender details" content={ <Fragment>Content goes here</Fragment> }/>);
        });

        it('contains label', () => {
            expect(wrapper.text()).to.contain('Offender details');
        });
        it('has enclosed hidden content by default', () => {
            expect(wrapper.find('.qa-accordion-content').hasClass('govuk-visually-hidden')).to.equal(true);
            expect(wrapper.text()).to.contain('Content goes here');
        });
        it('displays the enclosed content when state is changed', () => {
            wrapper.setState({ isOpen: 'true' });
            expect(wrapper.find('.qa-accordion-content').hasClass('govuk-visually-hidden')).to.equal(false);
        });
    });
});
