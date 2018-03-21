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
            feedbackPage.find('form').simulate('submit', {preventDefault});
        })

        it('adds feedback outcome', () => {
            expect(addFeedback).to.be.calledWith({rating: "Very satisfied", feedback: "Nothing - it is perfect"}, context.router)
        })

        it('will prevent default browser submit behaviour', () => {
            expect(preventDefault).to.be.calledOnce
        })
    })
})

