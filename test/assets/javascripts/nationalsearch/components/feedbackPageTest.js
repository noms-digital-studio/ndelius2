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
        beforeEach(() => {
            global.window = {
                location: {
                    href: ''
                }
            }
            goBack = stub()
            preventDefault = stub()
            const context = {
                router: {
                    goBack
                }
            };
            feedbackPage = shallow(<FeedbackPage/>, {context})
            feedbackPage.find('#rating_verysatisfied').simulate('change', {target: {value: 'Very satisfied'}})
            feedbackPage.find('#feedback').simulate('change', {target: {value: 'Nothing - it is perfect'}})
            feedbackPage.find('form').simulate('submit', {preventDefault});
        })

        it('sets location protocol to mailto:', () => {
            expect(global.window.location.href).to.contain("mailto:")
        })

        it('sets selected rating', () => {
            expect(decodeURIComponent(global.window.location.href)).to.contain("Very satisfied")
        })

        it('sets selected feedback', () => {
            expect(decodeURIComponent(global.window.location.href)).to.contain("Nothing - it is perfect")
        })

        it('will prevent default browser submit behaviour', () => {
            expect(preventDefault).to.be.calledOnce
        })

        it('will ask router to go back a page', () => {
            expect(goBack).to.be.calledOnce
        })

    })
})

