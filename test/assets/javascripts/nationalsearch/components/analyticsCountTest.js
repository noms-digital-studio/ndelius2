import AnalyticsCount  from './analyticsCount'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('AnalyticsCount component', () => {
    describe('rendering', () => {
        let panel
        beforeEach(() => {
            panel = shallow(<AnalyticsCount count={99} description={'some count'} fetching={false} />)
        })

        it('displays the count', () => {
            expect(panel.text()).to.contain('99')

        })
        it('displays description', () => {
            expect(panel.text()).to.contain('some count')
        })
    })
})

