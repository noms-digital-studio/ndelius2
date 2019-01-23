import OffenderSummaryPage from './offenderSummaryPage'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('OffenderSummaryPage component', () => {

    beforeEach(() => {
        global.document = {
            querySelectorAll: stub()
        }
    })

    context('on mount', () => {
        it('offender details are requested', () => {
            const getOffenderDetails = stub()
            shallow(<OffenderSummaryPage getOffenderDetails={getOffenderDetails} fetching={true} />)

            expect(getOffenderDetails).to.be.calledOnce
        })
    })


    describe('rendering', () => {
        let page

        context('when fetching', () => {
            beforeEach(() => {
                page = shallow(<OffenderSummaryPage getOffenderDetails={stub()} fetching={true} error={false} />)
            })

            it('no main content is displayed', () => {
                expect(page.find('.qa-main-content').exists()).to.be.false
            })
            it('banner is displayed', () => {
                expect(page.find('GovUkPhaseBanner').exists()).to.be.true
            })
        })
        context('when finished fetching', () => {
            beforeEach(() => {
                page = shallow(<OffenderSummaryPage getOffenderDetails={stub()} fetching={false} error={false}/>)
            })

            it('main content is displayed', () => {
                expect(page.find('.qa-main-content').exists()).to.be.true
            })
            it('offender identity container is displayed', () => {
                expect(page.find('Connect(OffenderIdentity)').exists()).to.be.true
            })
            it('offender serious registrations container is displayed', () => {
                expect(page.find('Connect(SeriousRegistrations)').exists()).to.be.true
            })
            it('offender registrations container is displayed', () => {
                expect(page.find('Connect(Registrations)').exists()).to.be.true
            })
            it('offender convictions container is displayed', () => {
                expect(page.find('Connect(Convictions)').exists()).to.be.true
            })
            it('offender notes container is displayed', () => {
                expect(page.find('Connect(Notes)').exists()).to.be.true
            })
            it('banner is displayed', () => {
                expect(page.find('GovUkPhaseBanner').exists()).to.be.true
            })
        })
        context('when in error', () => {
            beforeEach(() => {
                page = shallow(<OffenderSummaryPage getOffenderDetails={stub()} fetching={false} error={true} />)
            })

            it('no main content is displayed', () => {
                expect(page.find('.qa-main-content').exists()).to.be.false
            })
            it('error is displayed', () => {
                expect(page.find('ErrorMessage').exists()).to.be.true
            })
            it('banner is displayed', () => {
                expect(page.find('GovUkPhaseBanner').exists()).to.be.true
            })
        })
    })
})

