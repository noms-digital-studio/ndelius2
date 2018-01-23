import AddNewOffenderLink  from './addNewOffenderLink'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('AddNewOffenderLink component', () => {
    context('link clicked', () => {
        it('addNewOffender fcallback function called', () => {
            const addNewOffender = stub()
            const link = shallow(<AddNewOffenderLink addNewOffender={addNewOffender}/>)

            link.find('a').simulate('click');

            expect(addNewOffender).to.be.calledWith();
        })
    })
})

