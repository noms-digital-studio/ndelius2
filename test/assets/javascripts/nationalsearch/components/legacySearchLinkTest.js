import LegacySearchLink  from './legacySearchLink'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('LegacySearchLink component', () => {
    context('link clicked', () => {
        it('legacySearch callback function called', () => {
            const legacySearch = stub()
            const link = shallow(<LegacySearchLink legacySearch={legacySearch}>click here</LegacySearchLink>)

            link.find('a').simulate('click');

            expect(legacySearch).to.be.calledWith();
        })
    })
})

