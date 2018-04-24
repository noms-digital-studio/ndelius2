import FeedbackPage  from './feedbackPage'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('FeedbackPage component', () => {
    let feedbackPage
    describe('Rendering', () => {

        beforeEach(() => {
            const context = {
                router: {
                    goBack: stub()
                }
            };

            feedbackPage = shallow(<FeedbackPage/>, {context})
        })

        it('contains a form', () => {
            expect(feedbackPage.find('form').exists()).to.be.true
        })

        it('contains an email field', () => {
            expect(feedbackPage.find('#email').exists()).to.be.true
        })

        it('contains a role select field', () => {
            expect(feedbackPage.find('#role').exists()).to.be.true
        })

        it('contains an `other role` text field if `Other` is selected in the role drop down list', () => {
            feedbackPage.find('#role').simulate('change', {target: {value: 'Other'}})
            expect(feedbackPage.find('#role-other').exists()).to.be.true
        })

        it('does not contains an `other role` text field if `Other` is not selected in the role drop down list', () => {
            expect(feedbackPage.find('#role-other').exists()).to.be.false
        })

        it('contains a provider select field', () => {
            expect(feedbackPage.find('#provider').exists()).to.be.true
        })

        it('contains a region select field', () => {
            expect(feedbackPage.find('#region').exists()).to.be.true
        })

    })

    describe('submitting form', () => {
        let preventDefault;
        let goBack;
        let addFeedback;
        let context;
        beforeEach(() => {
            addFeedback = stub()
            goBack = stub()
            preventDefault = stub()
            context = {
                router: {
                    goBack
                }
            };
            feedbackPage = shallow(<FeedbackPage addFeedback={addFeedback}/>, {context})
            feedbackPage.find('#rating_verysatisfied').simulate('change', {target: {value: 'Very satisfied'}})
            feedbackPage.find('#feedback').simulate('change', {target: {value: 'Nothing - it is perfect'}})
            feedbackPage.find('#email').simulate('change', {target: {value: 'foo@bar.com'}})
            feedbackPage.find('#role').simulate('change', {target: {value: 'Offender Manager in the Community'}})
            feedbackPage.find('#provider').simulate('change', {target: {value: 'CRC'}})
            feedbackPage.find('#region').simulate('change', {target: {value: 'London'}})

        })

        it('adds feedback outcome', () => {
            feedbackPage.find('form').simulate('submit', {preventDefault});

            expect(addFeedback).to.be.calledWith({
                email: "foo@bar.com",
                rating: "Very satisfied",
                feedback: "Nothing - it is perfect",
                role: "Offender Manager in the Community",
                provider: "CRC",
                region: "London"
            }, context.router)
        })

        it('uses the other role text field when `Other` is selected in the role drop down list', () => {
            feedbackPage.find('#role').simulate('change', {target: {value: 'Other'}})
            feedbackPage.find('#role-other').simulate('change', {target: {value: 'Some other role'}})
            feedbackPage.find('form').simulate('submit', {preventDefault});

            expect(addFeedback).to.be.calledWith({
                email: "foo@bar.com",
                rating: "Very satisfied",
                feedback: "Nothing - it is perfect",
                role: "Some other role",
                provider: "CRC",
                region: "London"
            }, context.router)
        })

        it('uses `Other` when `Other` is selected in the role drop down list but no data is provided in the other role text field', () => {
            feedbackPage.find('#role').simulate('change', {target: {value: 'Other'}})
            feedbackPage.find('form').simulate('submit', {preventDefault});

            expect(addFeedback).to.be.calledWith({
                email: "foo@bar.com",
                rating: "Very satisfied",
                feedback: "Nothing - it is perfect",
                role: "Other",
                provider: "CRC",
                region: "London"
            }, context.router)
        })

        it('will prevent default browser submit behaviour', () => {
            feedbackPage.find('form').simulate('submit', {preventDefault});
            expect(preventDefault).to.be.calledOnce
        })
    })
})

