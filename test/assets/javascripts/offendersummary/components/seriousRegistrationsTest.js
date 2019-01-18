import SeriousRegistrations  from './seriousRegistrations'
import {expect} from 'chai';
import {shallow} from 'enzyme';

describe('Serious registrations component', () => {
    describe('rendering', () => {
        let wrapper

        context('with no registrations', () => {
            beforeEach(() => {
                wrapper = shallow(<SeriousRegistrations registrations={[]} />)
            })

            it('no message is displayed', () => {
                expect(wrapper.text()).to.equal('')
            })
        })
        context('with registrations where none are serious', () => {
            beforeEach(() => {
                wrapper = shallow(<SeriousRegistrations registrations={[
                  {warnUser: false},
                  {warnUser: false}
                ]} />)
            })

            it('no message is displayed', () => {
                expect(wrapper.text()).to.equal('')
            })
        })
        context('with registrations where one is serious', () => {
            beforeEach(() => {
                wrapper = shallow(<SeriousRegistrations registrations={[
                  {warnUser: false},
                  {warnUser: true},
                  {warnUser: false}
                ]} />)
            })

            it('message is displayed', () => {
                expect(wrapper.text()).to.contain('This offender has serious registrations')
            })
        })
        context('with registrations where more than one is serious', () => {
            beforeEach(() => {
                wrapper = shallow(<SeriousRegistrations registrations={[
                  {warnUser: false},
                  {warnUser: true},
                  {warnUser: true}
                ]} />)
            })

            it('message is displayed', () => {
                expect(wrapper.text()).to.contain('This offender has serious registrations')
            })
        })
    })
})

