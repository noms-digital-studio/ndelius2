import NextAppointment, {AppointmentDetail}  from './nextAppointment'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon'

describe('NextAppointment component', () => {
    const anAppointment = (appointment = {}) =>
        Object.assign(  {
            appointmentId: 2502592267,
            appointmentType: {
                code: "APPA03",
                description: "AP PA - Accommodation"
            },
            appointmentDate: "2019-03-28",
            appointmentStartTime: "09:28:00",
            appointmentEndTime: "10:28:00",
            createdDateTime: "2019-01-09T09:29:50",
            lastUpdatedDateTime: "2019-01-09T09:29:50",
            staff: {
                forenames: "Chloe ZZ",
                surname: "Ransom"
            },
            team: {
                code: "N07T01",
                description: "OMU A"
            },
            officeLocation: {
                code: "LDN_BCS",
                description: "1 REGARTH AVENUE"
            },
            probationArea: {
                code: "N07",
                description: "NPS London"
            },
            visorContact: false,
            attended: "NOT_RECORDED"
        }, appointment)

    context('on mount', () => {
        it('next appointment is requested', () => {
            const getNextAppointment = stub()
            shallow(<NextAppointment error={false} fetching={true} getNextAppointment={getNextAppointment}/>)

            expect(getNextAppointment).to.be.calledOnce
        })
    })
    
    describe('rendering NextAppointment', () => {
        let wrapper

        context('when fetching', () => {
            beforeEach(() => {
                wrapper = shallow(<NextAppointment fetching={true} error={false} getNextAppointment={stub()}/>)
            })

            it('no main content is displayed', () => {
                expect(wrapper.find('AppointmentDetail').exists()).to.be.false
            })
        })
        context('when finished fetching', () => {
            beforeEach(() => {
                wrapper = shallow(<NextAppointment  fetching={false} error={false} appointment={anAppointment(
                    {
                        probationArea: {
                            description: "NPS London"
                        },
                        appointmentDate: '2019-03-01',
                        appointmentType: {
                            description: 'Help with jobs'
                        }})} getNextAppointment={stub()}/>)
            })

            it('main content is displayed', () => {
                expect(wrapper.find('AppointmentDetail').exists()).to.be.true
            })

        })
        context('when in error', () => {
            beforeEach(() => {
                wrapper = shallow(<NextAppointment  fetching={false} error={true} getNextAppointment={stub()}/>)
            })

            it('no main content is displayed', () => {
                expect(wrapper.find('AppointmentDetail').exists()).to.be.false
            })
            it('error is displayed', () => {
                expect(wrapper.find('ErrorMessage').exists()).to.be.true
            })
        })
    })
    describe('AppointmentDetails component', () => {
        let wrapper

        beforeEach(() => {
            global.window = {
                GOVUKFrontend: {
                    Details: stub()
                }
            }
            global.window.GOVUKFrontend.Details.prototype.init = stub()
        })

        context('on mount', () => {
            it('details section is initialised', () => {
                shallow(<AppointmentDetail appointment={anAppointment()}/>)

                expect(global.window.GOVUKFrontend.Details.prototype.init ).to.be.calledOnce
            })
        })


        context('rendering AppointmentDetails', () => {
            beforeEach(() => {
                wrapper = shallow(<AppointmentDetail appointment={anAppointment(
                    {
                        probationArea: {
                            description: "NPS London"
                        },
                        appointmentDate: '2019-03-01',
                        appointmentType: {
                            description: 'Help with jobs'
                        }})}/>)
            })

            it('appointment type is rendered', () => {
                expect(wrapper.find('tbody tr').at(0).find('td').text()).to.equal('Help with jobs')
            })

            it('appointment date is rendered formatted', () => {
                expect(wrapper.find('tbody tr').at(1).find('td').text()).to.equal('01/03/2019')
            })

            it('provider is rendered', () => {
                expect(wrapper.find('tbody tr').at(4).find('td').text()).to.equal('NPS London')
            })

            context('when appointment time in morning', () => {
                beforeEach(() => {
                    wrapper = shallow(<AppointmentDetail appointment={anAppointment(
                        {
                            appointmentStartTime: '11:59:00'
                        })}/>)
                })
                it('appointment time is rendered formatted', () => {
                    expect(wrapper.find('tbody tr').at(2).find('td').text()).to.equal('11:59')
                })
            })
            context('when appointment time in afternoon', () => {
                beforeEach(() => {
                    wrapper = shallow(<AppointmentDetail  fetching={false} error={false} appointment={anAppointment(
                        {
                            appointmentStartTime: '14:59:00'
                        })} />)
                })
                it('appointment time is rendered formatted', () => {
                    expect(wrapper.find('tbody tr').at(2).find('td').text()).to.equal('14:59')
                })
            })

            context('when no appointment time', () => {
                beforeEach(() => {
                    const appointment = anAppointment()
                    delete appointment.appointmentStartTime
                    wrapper = shallow(<AppointmentDetail appointment={appointment}/>)
                })
                it('appointment time is rendered is Unknown', () => {
                    expect(wrapper.find('tbody tr').at(2).find('td').text()).to.equal('Unknown')
                })
            })

            context('when there is a location', () => {
                beforeEach(() => {
                    wrapper = shallow(<AppointmentDetail appointment={anAppointment(
                        {
                            officeLocation: {
                                description: '1 High Street'
                            }
                        })}/>)
                })
                it('location is rendered', () => {
                    expect(wrapper.find('tbody tr').at(3).find('td').text()).to.equal('1 High Street')
                })

            })
            context('when there is no location', () => {
                beforeEach(() => {
                    const appointment = anAppointment()
                    delete appointment.officeLocation
                    wrapper = shallow(<AppointmentDetail appointment={appointment}/>)
                })
                it('location is rendered as Unknown', () => {
                    expect(wrapper.find('tbody tr').at(3).find('td').text()).to.equal('Unknown')
                })

            })
            context('when there is a team', () => {
                beforeEach(() => {
                    wrapper = shallow(<AppointmentDetail appointment={anAppointment(
                        {
                            team: {
                                description: 'OMU Z'
                            }
                        })}/>)
                })
                it('team is rendered', () => {
                    expect(wrapper.find('tbody tr').at(5).find('td').text()).to.equal('OMU Z')
                })

            })
            context('when there is no team', () => {
                beforeEach(() => {
                    const appointment = anAppointment()
                    delete appointment.team
                    wrapper = shallow(<AppointmentDetail appointment={appointment}/>)
                })
                it('team is rendered as Unknown', () => {
                    expect(wrapper.find('tbody tr').at(5).find('td').text()).to.equal('Unknown')
                })

            })
            context('when there is an officer', () => {
                beforeEach(() => {
                    wrapper = shallow(<AppointmentDetail appointment={anAppointment(
                        {
                            staff: {
                                forenames: "Chloe Sandra",
                                surname: "Ransom"
                            }
                        })} />)
                })
                it('team is rendered', () => {
                    expect(wrapper.find('tbody tr').at(6).find('td').text()).to.equal('Ransom, Chloe Sandra')
                })

            })
            context('when there no officer', () => {
                beforeEach(() => {
                    const appointment = anAppointment()
                    delete appointment.staff
                    wrapper = shallow(<AppointmentDetail appointment={appointment}/>)
                })
                it('team is rendered', () => {
                    expect(wrapper.find('tbody tr').at(6).find('td').text()).to.equal('Unknown')
                })

            })
            context('when there officer is unallocated', () => {
                beforeEach(() => {
                    wrapper = shallow(<AppointmentDetail appointment={anAppointment(
                        {
                            staff: {
                                forenames: 'Unallocated Staff(N02)',
                                surname: 'Staff'
                            }
                        })} />)
                })
                it('team is rendered', () => {
                    expect(wrapper.find('tbody tr').at(6).find('td').text()).to.equal('Unallocated')
                })

            })
        })
    })
})
