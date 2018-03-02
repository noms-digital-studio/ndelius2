import RestrictedOffenderSearchSummary  from './restrictedOffenderSearchSummary'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';
import {offender} from '../test-helper'

describe('RestrictedOffenderSearchSummary component', () => {
    describe('rendering', () => {
        it('should render crn', () => {
            const offenderSummary = offender({
                otherIds: {
                    crn: 'X12343'
                    }})

            const summary = shallow(<RestrictedOffenderSearchSummary
                                        offenderSummary={offenderSummary}
                                        showOffenderDetails={()=>{}}/>)


            expect(summary.text()).to.contain('X12343')
        })
        it('should render add contact link', () => {
            const summary = shallow(<RestrictedOffenderSearchSummary
                                        offenderSummary={offender()}
                                        showOffenderDetails={()=>{}}/>)


            expect(summary.find('Connect(AddContactLink)')).to.have.length(1)
        })
    })
    context('link clicked', () => {
        it('showOffenderDetails callback function called with offenderId', () => {
            const showOffenderDetails = stub()
            const offenderSummary = offender({offenderId: 123})

            const summary = shallow(<RestrictedOffenderSearchSummary
                                        offenderSummary={offenderSummary}
                                        showOffenderDetails={showOffenderDetails}/>)

            summary.find('a').simulate('click');

            expect(showOffenderDetails).to.be.calledWith(123);
        })
    })
})

