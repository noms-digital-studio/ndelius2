import AnalyticsPieChart, {chartOptions}  from './analyticsPieChart'
import {expect} from 'chai';
import {shallow} from 'enzyme';

describe('AnalyticsPieChart component', () => {
    describe('rendering', () => {
        it('canvas rendered', () => {
            const chart = shallow(<AnalyticsPieChart description='My Chart' label='My Label' fetching={false} numberToCountData={{"1" : 10, "2": 20}} />)

            expect(chart.find('canvas').exists()).to.be.true
        })
    })

    describe('chartOptions', () => {
        let options

        beforeEach(() => {
            options = chartOptions({
                description:'My Chart',
                label: 'My Label',
                fetching: false,
                numberToCountData: {"1" : 10, "2": 20}
            })
        })

        it('sets labels to data keys', () => {
            expect(options.data.labels).to.eql(["1", "2"])
        })

        it('sets data to data values', () => {
            expect(options.data.datasets[0].data).to.eql([10, 20])
        })

        it('sets label on first data set', () => {
            expect(options.data.datasets[0].label).to.equal('My Label')
        })

        context('with label mapper', () => {
            beforeEach(() => {
                options = chartOptions({
                    description:'My Chart',
                    label: 'My Label',
                    fetching: false,
                    numberToCountData: {"1" : 10, "2": 20},
                    labelMapper: (data) => Object.getOwnPropertyNames(data).map((name, index) => 'Chickens ' + index)
                })
            })

            it('sets labels using mapper', () => {
                expect(options.data.labels).to.eql(["Chickens 0", "Chickens 1"])
            })


        })

    })
})

