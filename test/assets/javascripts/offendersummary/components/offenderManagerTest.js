import OffenderManager  from './offenderManager'
import {expect} from 'chai';
import {shallow} from 'enzyme';

describe('OffenderManager component', () => {
    describe('rendering', () => {
        let wrapper

        context('when fetching', () => {
            beforeEach(() => {
                wrapper = shallow(<OffenderManager fetching={true} error={false} />)
            })

            it('no main content is displayed', () => {
                expect(wrapper.find('.qa-offender-manager').exists()).to.be.false
            })
        })
        context('when finished fetching', () => {
            beforeEach(() => {
                wrapper = shallow(<OffenderManager  fetching={false} error={false} />)
            })

            it('main content is displayed', () => {
                expect(wrapper.find('.qa-offender-manager').exists()).to.be.true
            })
            it('next appointment is displayed', () => {
                expect(wrapper.find('Connect(NextAppointment)').exists()).to.be.true
            })
        })
        context('when in error', () => {
            beforeEach(() => {
                wrapper = shallow(<OffenderManager  fetching={false} error={true} />)
            })

            it('no main content is displayed', () => {
                expect(wrapper.find('.qa-offender-manager').exists()).to.be.false
            })
            it('error is displayed', () => {
                expect(wrapper.find('ErrorMessage').exists()).to.be.true
            })
        })
    })
})

