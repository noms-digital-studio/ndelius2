import OffenderSummaryTitle  from './offenderSummaryTitle'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('OffenderSummaryTitle component', () => {
    context('link clicked', () => {
        it('showOffenderDetails callback function called with offenderId', () => {
            const showOffenderDetails = stub()
            const title = shallow(<OffenderSummaryTitle offenderId={123}
                                                        showOffenderDetails={showOffenderDetails}
                                                        firstName={'name'}
                                                        surname={'name'}
                                                        dateOfBirth={'dob'}/>)

            title.find('a').simulate('click');

            expect(showOffenderDetails).to.be.calledWith(123);
        })
    })
})

