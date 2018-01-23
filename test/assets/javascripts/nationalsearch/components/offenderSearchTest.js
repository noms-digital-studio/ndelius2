import OffenderSearch  from './offenderSearch'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('OffenderSearch component', () => {
    context('search term typed in', () => {
        it('search callback function called with search term', () => {
            const search = stub()
            const searchBox = shallow(<OffenderSearch search={search} searchTerm={'Mr Bean'}/>)

            searchBox.find('input').simulate('change', {target: {value: 'Mr Beans'}});

            expect(search).to.be.calledWith('Mr Beans');
        })
    })
    context('form submitted', () => {
        let search
        let preventDefault
        let searchBox

        beforeEach(() => {
            search = stub()
            preventDefault = stub()
            searchBox = shallow(<OffenderSearch search={search} searchTerm={'Mr Bean'}/>)
        })
        it('search callback function called with search term', () => {
            searchBox.find('form').simulate('submit', {preventDefault});

            expect(search).to.be.calledWith('Mr Bean');
        })
        it('submit default behaviour is prevented', () => {
            searchBox.find('form').simulate('submit', {preventDefault});

            expect(preventDefault).to.be.calledWith();
        })
    })
})

