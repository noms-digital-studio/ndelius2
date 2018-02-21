import AnalyticsTimeRangeLink  from './analyticsTimeRangeLink'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';
import {LAST_SEVEN_DAYS, LAST_THIRTY_DAYS} from '../actions/analytics'

describe('AnalyticsTimeRangeLink component', () => {
    describe('rendering', () => {
        context('current time range matches this components range', () => {
            it('sets font weight to bold', () => {
                const link = shallow(<AnalyticsTimeRangeLink
                    timeRange={LAST_SEVEN_DAYS}
                    currentTimeRange={LAST_SEVEN_DAYS}
                    changeTimeRange={stub()}>Click me</AnalyticsTimeRangeLink>)

                expect(link.find('a').prop('style').fontWeight).to.equal('bold')
            })
        })
        context('current time range does not match this components range', () => {
            it('sets font weight to normal', () => {
                const link = shallow(<AnalyticsTimeRangeLink
                    timeRange={LAST_SEVEN_DAYS}
                    currentTimeRange={LAST_THIRTY_DAYS}
                    changeTimeRange={stub()}>Click me</AnalyticsTimeRangeLink>)

                expect(link.find('a').prop('style').fontWeight).to.equal('normal')
            })
        })
    })
    context('link clicked', () => {
        it('changeTimeRange callback function called with timeRange', () => {
            const changeTimeRange = stub()
            const link = shallow(<AnalyticsTimeRangeLink
                timeRange={LAST_SEVEN_DAYS}
                currentTimeRange={LAST_THIRTY_DAYS}
                changeTimeRange={changeTimeRange}>Click me</AnalyticsTimeRangeLink>)

            link.find('a').simulate('click')

            expect(changeTimeRange).to.be.calledWith(LAST_SEVEN_DAYS)
        })
    })
})

