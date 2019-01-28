import OffenderManager  from './offenderManager'
import {expect} from 'chai';
import {shallow} from 'enzyme';

describe('OffenderManager component', () => {
    const aOffenderManager = (offenderManager = {}) =>
        Object.assign(    {
                trustOfficer: {
                    forenames: "Sophie Zz",
                    surname: "Pheim"
                },
                staff: {
                    forenames: "Sophie Zz",
                    surname: "Pheim"
                },
                partitionArea: "National Data",
                softDeleted: false,
                team: {
                    description: "OMU A",
                    telephone: "0114 555 1236",
                    district: {
                        code: "N04NPSA",
                        description: "N04 Division"
                    },
                    borough: {
                        code: "N04100",
                        description: "N04 Cluster 1"
                    }
                },
                probationArea: {
                    code: "N04",
                    description: "NPS Midlands"
                },
                fromDate: "2017-09-18",
                active: true,
                allocationReason: {
                    code: "OM",
                    description: "Offender Moved"
                }
            }, offenderManager)
    
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
                wrapper = shallow(<OffenderManager  fetching={false} error={false} offenderManager={aOffenderManager(
                    {
                        probationArea: {
                            description: 'NPS North West'
                        },
                        fromDate: '2019-03-01'
                    }
                )}/>)
            })

            it('main content is displayed', () => {
                expect(wrapper.find('.qa-offender-manager').exists()).to.be.true
            })

            it('personal circumstances is displayed', () => {
                expect(wrapper.find('Connect(PersonalCircumstances)').exists()).to.be.true
            })

            it('next appointment is displayed', () => {
                expect(wrapper.find('Connect(NextAppointment)').exists()).to.be.true
            })

            it('probation area is rendered', () => {
                expect(wrapper.find('tbody tr').at(0).find('td').text()).to.equal('NPS North West')
            })

            it('from date is rendered formatted', () => {
                expect(wrapper.find('tbody tr').at(6).find('td').text()).to.equal('01/03/2019')
            })

            context('with a team', () => {
                beforeEach(() => {
                    wrapper = shallow(<OffenderManager  fetching={false} error={false} offenderManager={aOffenderManager(
                        {
                            team: {
                                description: 'OMU B',
                                borough: {
                                    description: 'Battersea'
                                },
                                district: {
                                    description: 'Inner London'
                                },
                                telephone: '020 555 1234'
                            }
                        }
                    )}/>)
                })
                it('cluster is rendered using borough', () => {
                    expect(wrapper.find('tbody tr').at(1).find('td').text()).to.equal('Battersea')
                })
                it('LDU is rendered', () => {
                    expect(wrapper.find('tbody tr').at(2).find('td').text()).to.equal('Inner London')
                })
                it('Team is rendered', () => {
                    expect(wrapper.find('tbody tr').at(3).find('td').text()).to.equal('OMU B')
                })
                it('telephone is rendered', () => {
                    expect(wrapper.find('tbody tr').at(5).find('td').text()).to.equal('020 555 1234')
                })
            })
            context('without a team', () => {
                beforeEach(() => {
                    const offenderManager = aOffenderManager()
                    delete offenderManager.team
                    wrapper = shallow(<OffenderManager  fetching={false} error={false} offenderManager={offenderManager}/>)
                })
                it('cluster is rendered as Unknown', () => {
                    expect(wrapper.find('tbody tr').at(1).find('td').text()).to.equal('Unknown')
                })
                it('LDU is rendered as Unknown', () => {
                    expect(wrapper.find('tbody tr').at(2).find('td').text()).to.equal('Unknown')
                })
                it('Team is rendered as Unknown', () => {
                    expect(wrapper.find('tbody tr').at(3).find('td').text()).to.equal('Unknown')
                })
                it('telephone is rendered as Unknown', () => {
                    expect(wrapper.find('tbody tr').at(5).find('td').text()).to.equal('Unknown')
                })
            })

            context('with allocation reason', () => {
                beforeEach(() => {
                    wrapper = shallow(<OffenderManager  fetching={false} error={false} offenderManager={aOffenderManager(
                        {
                            allocationReason: {
                                description: 'Offender moved'
                            }
                        }
                    )}/>)
                })
                it('allocation is rendered', () => {
                    expect(wrapper.find('tbody tr').at(7).find('td').text()).to.equal('Offender moved')
                })
            })
            context('without allocation reason', () => {
                beforeEach(() => {
                    const offenderManager = aOffenderManager()
                    delete offenderManager.allocationReason
                    wrapper = shallow(<OffenderManager fetching={false} error={false}
                                                       offenderManager={offenderManager}/>)
                })
                it('allocation is rendered as Unknown', () => {
                    expect(wrapper.find('tbody tr').at(7).find('td').text()).to.equal('Unknown')
                })
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
