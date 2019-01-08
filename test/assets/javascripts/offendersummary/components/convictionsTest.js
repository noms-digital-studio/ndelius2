import Convictions  from './convictions'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('Convictions component', () => {
    let wrapper
    const someConvictions = () => [aConviction({convictionId: 1}), aConviction({convictionId: 2})]

    const aConviction = (conviction = {}) =>
        Object.assign({
            convictionId: 2500281204,
            active: true,
            inBreach: false,
            convictionDate: "2018-12-18",
            referralDate: "2018-12-18",
            offences: [
                {
                    offenceId: "M2500281204",
                    mainOffence: true,
                    detail: {
                        code: "08902",
                        description: "Adulteration etc of milk products - 08902",
                        mainCategoryCode: "089",
                        mainCategoryDescription: "Adulteration of food or drugs (Food Safety Act 1990)",
                        mainCategoryAbbreviation: "Adulteration of food or drugs (Food Safety Act 19",
                        ogrsOffenceCategory: "Other offence",
                        subCategoryCode: "02",
                        subCategoryDescription: "Adulteration etc of milk products",
                        form20Code: "12"
                    },
                    offenceDate: "2018-12-02T00:00:00",
                    offenceCount: 1,
                    offenderId: 2500099840,
                    createdDatetime: "2018-12-18T10:53:33",
                    lastUpdatedDatetime: "2018-12-18T10:53:33"
                }
            ],
            sentence: {
                description: "ORA Community Order",
                originalLength: 18,
                originalLengthUnits: "Months",
                defaultLength: 18,
                lengthInDays: 547
            },
            latestCourtAppearanceOutcome: {
                code: "329",
                description: "ORA Community Order"
            }
            }, conviction)


    context('on mount', () => {
        it('offender convictions are requested', () => {
            const getOffenderConvictions = stub()
            shallow(<Convictions convictions={[]} error={false} fetching={true} getOffenderConvictions={getOffenderConvictions} maxConvictionsVisible={3}/>)

            expect(getOffenderConvictions).to.be.calledOnce
        })
    })


    describe('conviction rendering', () => {
        context('no convictions', () => {
            beforeEach(() => {
                wrapper = shallow(<Convictions convictions={[]} error={false} fetching={false} getOffenderConvictions={stub()} maxConvictionsVisible={3}/>)
            })

            it('contains conviction count of zero', () => {
                expect(wrapper.find({label: "Events (0)"}).exists()).to.be.true
            })

            it('contains no data text', () => {
                expect(wrapper.find('.qa-offender-convictions').text()).to.equal('No data')
            })

        })

        context('some convictions', () => {
            beforeEach(() => {
                wrapper = shallow(<Convictions convictions={someConvictions()} error={false} fetching={false} getOffenderConvictions={stub()} maxConvictionsVisible={3}/>)
            })

            it('contains conviction count', () => {
                expect(wrapper.find({label: "Events (2)"}).exists()).to.be.true
            })
            it('contains 2 rows for each conviction', () => {
                expect(wrapper.find('tbody tr')).to.have.length(4)
            })
        })

        context('single conviction', () => {
            beforeEach(() => {
                wrapper = shallow(<Convictions convictions={[
                    aConviction({
                        convictionId: 1,
                        referralDate: "2018-10-18",
                        active: true,
                        offences: [
                            {
                                offenceId: "1",
                                mainOffence: false,
                                detail: {
                                    code: "08902",
                                    description: "Cooking rats - 08902"
                                }
                            },
                            {
                                offenceId: "2",
                                mainOffence: true,
                                detail: {
                                    code: "08902",
                                    description: "Cooking foxes - 08902"
                                }
                            },
                            {
                                offenceId: "3",
                                mainOffence: false,
                                detail: {
                                    code: "08902",
                                    description: "Cooking humans - 08902"
                                }
                            }

                        ]

                })]} error={false} fetching={false} getOffenderConvictions={stub()} maxConvictionsVisible={3}/>)
            })

            it('contains main offence', () => {
                expect(wrapper.find('tbody tr td').at(1).text()).to.equal('Cooking foxes - 08902')
            })
            it('contains formatted referral date', () => {
                expect(wrapper.find('tbody tr td').at(2).text()).to.equal('18/10/2018')
            })
        })
        context('single active conviction', () => {
            beforeEach(() => {
                wrapper = shallow(<Convictions convictions={[
                    aConviction({
                        convictionId: 1,
                        active: true

                })]} error={false} fetching={false} getOffenderConvictions={stub()} maxConvictionsVisible={3}/>)
            })

            it('contains conviction status in green', () => {
                expect(wrapper.find('tbody tr td').at(3).text()).to.equal('Active')
            })
            it('displays conviction status in red', () => {
                expect(wrapper.find('tbody tr td').at(3).find('p').hasClass('moj-!-color-green')).to.equal(true)
            })
        })

        context('single inactive conviction', () => {
            beforeEach(() => {
                wrapper = shallow(<Convictions convictions={[
                    aConviction({
                        convictionId: 1,
                        active: false

                })]} error={false} fetching={false} getOffenderConvictions={stub()} maxConvictionsVisible={3}/>)
            })

            it('contains conviction status of terminated', () => {
                expect(wrapper.find('tbody tr td').at(3).text()).to.equal('Terminated')
            })
            it('displays conviction status in red', () => {
                expect(wrapper.find('tbody tr td').at(3).find('p').hasClass('moj-!-color-red')).to.equal(true)
            })
        })

        context('a conviction with a sentence', () => {
            beforeEach(() => {
                wrapper = shallow(<Convictions convictions={[
                    aConviction({
                        sentence: {
                            description: "Cold bloodied murder",
                            originalLength: 999,
                            originalLengthUnits: "Months"
                        },
                        latestCourtAppearanceOutcome: {
                            code: "329",
                            description: "Bail Review"
                        }
                    })
                ]} error={false} fetching={false} getOffenderConvictions={stub()} maxConvictionsVisible={3}/>)
            })

            it('uses sentence has title', () => {
                expect(wrapper.find('tbody tr').at(0).text()).to.equal('Cold bloodied murder (999 Months)')
            })

        })
        context('a conviction without a sentence', () => {
            beforeEach(() => {
                const conviction = aConviction({
                    latestCourtAppearanceOutcome: {
                        code: "329",
                        description: "Bail Review"
                    }
                })
                delete conviction.sentence
                wrapper = shallow(<Convictions convictions={[
                    conviction
                ]} error={false} fetching={false} getOffenderConvictions={stub()} maxConvictionsVisible={3}/>)
            })

            it('uses last court outcome as title', () => {
                expect(wrapper.find('tbody tr').at(0).text()).to.equal('Bail Review')
            })

        })

        describe('sorting', () => {
            beforeEach(() => {
                wrapper = shallow(<Convictions convictions={[
                    aConviction({
                        convictionId: 1,
                        referralDate: "2018-10-18",
                        sentence: {
                            description: "October conviction",
                            originalLength: 18,
                            originalLengthUnits: "Months"
                        }
                    }),
                    aConviction({
                        convictionId: 2,
                        referralDate: "2018-12-18",
                        sentence: {
                            description: "December conviction",
                            originalLength: 18,
                            originalLengthUnits: "Months"
                        }
                    }),
                    aConviction({
                        convictionId: 3,
                        referralDate: "2018-11-18",
                        sentence: {
                            description: "November conviction",
                            originalLength: 18,
                            originalLengthUnits: "Months"
                        }
                    })
                ]} error={false} fetching={false} getOffenderConvictions={stub()} maxConvictionsVisible={3}/>)
            })

            it('is ordered by referral date', () => {
                expect(wrapper.find('tbody tr').at(0).text()).to.equal('December conviction (18 Months)')
                expect(wrapper.find('tbody tr').at(1*2).text()).to.equal('November conviction (18 Months)')
                expect(wrapper.find('tbody tr').at(2*2).text()).to.equal('October conviction (18 Months)')
            })

        })

        describe('show more events link', () => {
            let showMoreConvictions;
            beforeEach(() => {
                showMoreConvictions = stub();
            })


            context('with 4 convictions', () => {
                const ROWS_PER_CONVICTIONS = 2;
                beforeEach(() => {
                    showMoreConvictions = stub();
                    wrapper = shallow(<Convictions convictions={[
                        aConviction({convictionId: 1}),
                        aConviction({convictionId: 2}),
                        aConviction({convictionId: 3}),
                        aConviction({convictionId: 4})
                    ]} error={false} fetching={false} getOffenderConvictions={stub()} showMoreConvictions={showMoreConvictions} maxConvictionsVisible={3}/>)
                })
                it('contains conviction count', () => {
                    expect(wrapper.find({label: "Events (4)"}).exists()).to.be.true
                })
                it('shows 3 conviction', () => {
                    expect(wrapper.find('tbody tr')).to.have.length(3*ROWS_PER_CONVICTIONS)
                })
                it('should show a link to show more events', () => {
                    expect(wrapper.find({children: 'Show more events'}).exists()).to.be.true
                })
                it('clicking show more events calls showMoreConvictions callback', () => {
                    wrapper.find({children: 'Show more events'}).simulate('click')
                    expect(showMoreConvictions).to.be.calledOnce
                })
            })
            context('with 3 convictions', () => {
                const ROWS_PER_CONVICTIONS = 2;
                beforeEach(() => {
                    showMoreConvictions = stub();
                    wrapper = shallow(<Convictions convictions={[
                        aConviction({convictionId: 1}),
                        aConviction({convictionId: 2}),
                        aConviction({convictionId: 3})
                    ]} error={false} fetching={false} getOffenderConvictions={stub()} showMoreConvictions={showMoreConvictions} maxConvictionsVisible={3}/>)
                })
                it('contains conviction count', () => {
                    expect(wrapper.find({label: "Events (3)"}).exists()).to.be.true
                })
                it('shows 3 conviction', () => {
                    expect(wrapper.find('tbody tr')).to.have.length(3*ROWS_PER_CONVICTIONS)
                })
                it('should not show a link to show more events', () => {
                    expect(wrapper.find({children: 'Show more events'}).exists()).to.be.false
                })
            })
        })
    })
})

