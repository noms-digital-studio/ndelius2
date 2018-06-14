import SearchFooter  from './searchFooter'
import {expect} from 'chai';
import {shallow} from 'enzyme';

describe('SearchFooter component', () => {
    context('First time displaying the application', () => {
        let searchFooter

        beforeEach(() => {
            searchFooter = shallow(<SearchFooter/>)
        })

        it('contains a initial help text', () => {
            expect(searchFooter.find('.background-box')).to.have.length(1);
        })

    })
})

