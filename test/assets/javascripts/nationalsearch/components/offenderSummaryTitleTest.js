import OffenderSummaryTitle  from './offenderSummaryTitle'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('OffenderSummaryTitle component', () => {
    describe('date rendering', () => {
        it('should render date in dd/mm/yyyy format', () => {
            const title = shallow(<OffenderSummaryTitle offenderId={123}
                                                        rankIndex={3}
                                                        showOffenderDetails={()=>{}}
                                                        firstName={'name'}
                                                        surname={'name'}
                                                        dateOfBirth={'1965-07-19'}/>)


            expect(title.find({text: '19/07/1965', isDate: true})).to.have.length(1)
        })
    })
    context('link clicked', () => {
        it('showOffenderDetails callback function called with offenderId', () => {
            const showOffenderDetails = stub()
            const title = shallow(<OffenderSummaryTitle offenderId={123}
                                                        rankIndex={3}
                                                        showOffenderDetails={showOffenderDetails}
                                                        firstName={'name'}
                                                        surname={'name'}
                                                        dateOfBirth={'1965-07-19'}/>)

            title.find('a').simulate('click');

            expect(showOffenderDetails).to.be.calledWith(123, 3);
        })
    })
})

