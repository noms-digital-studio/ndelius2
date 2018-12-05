import OffenderSummaryPage from './offenderSummaryPage'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('OffenderSummaryPage component', () => {
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
                page = shallow(<OffenderSummaryPage getOffenderDetails={stub()} fetching={true} />)
            })

            it('no main content is displayed', () => {
                expect(page.find('.govuk-grid-row').exists()).to.be.false
            })
            it('banner is displayed', () => {
                expect(page.find('GovUkPhaseBanner').exists()).to.be.true
            })
        })
        context('when finished fetching', () => {
            beforeEach(() => {
                page = shallow(<OffenderSummaryPage getOffenderDetails={stub()} fetching={false} />)
            })

            it('main content is displayed', () => {
                expect(page.find('.govuk-grid-row').exists()).to.be.true
            })
            it('offender identity container is displayed', () => {
                expect(page.find('Connect(OffenderIdentity)').exists()).to.be.true
            })
            it('banner is displayed', () => {
                expect(page.find('GovUkPhaseBanner').exists()).to.be.true
            })
        })
    })
})

