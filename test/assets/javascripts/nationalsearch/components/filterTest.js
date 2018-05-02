import Filter  from './filter'
import {expect} from 'chai';
import {shallow} from 'enzyme';
import {stub} from 'sinon';

describe('Filter component', () => {
    let addToFilter;
    let removeFromFilter;
    let search;

    beforeEach(() => {
        addToFilter = stub()
        removeFromFilter = stub()
        search = stub()
    })
    describe ('area rendering', () => {
        context('with no filterValues', () => {
            it('no filter tables rendered', () => {
                const filter = shallow(<Filter
                    searchTerm='Mr Bean'
                    filterValues={[]}
                    currentFilter={[]}
                    addToFilter={addToFilter}
                    removeFromFilter={removeFromFilter}
                    search={search}
                    name='some-filter'
                    title='Some filter'
                />)
                expect(filter.find('.filter')).to.have.length(0)
            })
        })
        context('with filterValues', () => {
            it('filter table is rendered', () => {
                const filter = shallow(<Filter
                    searchTerm='Mr Bean'
                    filterValues={[{code: 'N01', description: 'Some Area', count: 67}]}
                    currentFilter={[]}
                    addToFilter={addToFilter}
                    removeFromFilter={removeFromFilter}
                    search={search}
                    name='some-filter'
                    title='Some filter'
                />)
                expect(filter.find('.filter')).to.have.length(1)
            })
        })
        context('with many filterValues', () => {
            it('filter row is rendered for each area', () => {
                const filter = shallow(<Filter
                    searchTerm='Mr Bean'
                    filterValues={[{code: 'N01', description: 'Some Area', count: 67}, {code: 'N02', description: 'Some Other Area', count: 4}]}
                    currentFilter={[]}
                    addToFilter={addToFilter}
                    removeFromFilter={removeFromFilter}
                    search={search}
                    name='some-filter'
                    title='Some filter'
                />)
                expect(filter.find('.filter label')).to.have.length(2)
            })
        })
    })

    describe ('area selection rendering', () => {
        let filter;
        context('when areaFilter empty', () => {
            beforeEach(() => {
                filter = shallow(<Filter
                    searchTerm='Mr Bean'
                    filterValues={[{code: 'N01', description: 'Some Area', count: 67}]}
                    currentFilter={[]}
                    addToFilter={addToFilter}
                    removeFromFilter={removeFromFilter}
                    search={search}
                    name='some-filter'
                    title='Some filter'
                />)

            })
            it('0 selected in selection hint text', () => {
                expect(filter.find('#selected').text()).to.equal('0 selected')
            })
            it('no boxes checked', () => {
                expect(filter.find({checked: true})).to.have.length(0)
            })
        })
        context('when areaFilter has one item', () => {
            beforeEach(() => {
                filter = shallow(<Filter
                    searchTerm='Mr Bean'
                    filterValues={[{code: 'N01', description: 'Some Area', count: 67}]}
                    currentFilter={['N01']}
                    addToFilter={addToFilter}
                    removeFromFilter={removeFromFilter}
                    search={search}
                    name='some-filter'
                    title='Some filter'
                />)

            })
            it('1 selected in selection hint text', () => {
                expect(filter.find('#selected').text()).to.equal('1 selected')
            })
            it('area checkbox should be checked', () => {
                expect(filter.find({value: 'N01'}).prop('checked')).to.be.true
            })
        })
        context('when areaFilter has many item', () => {
            beforeEach(() => {
                filter = shallow(<Filter
                    searchTerm='Mr Bean'
                    filterValues={[{code: 'N01', description: 'Some Area', count: 67}, {code: 'N02', description: 'Some Other Area', count: 4}, {code: 'N03', description: 'Some Other Area', count: 4}]}
                    currentFilter={['N01', 'N03']}
                    addToFilter={addToFilter}
                    removeFromFilter={removeFromFilter}
                    search={search}
                    name='some-filter'
                    title='Some filter'
                />)

            })
            it('2 selected in selection hint text', () => {
                expect(filter.find('#selected').text()).to.equal('2 selected')
            })
        })
    })

    describe('selection filter areas', () => {
        let filter;
        beforeEach(() => {
            filter = shallow(<Filter
                searchTerm='Mr Bean'
                filterValues={[{code: 'N01', description: 'N01 Area', count: 67}, {code: 'N02', description: 'N02 Area', count: 4}, {code: 'N03', description: 'N03 Area', count: 4}]}
                currentFilter={['N01', 'N03']}
                addToFilter={addToFilter}
                removeFromFilter={removeFromFilter}
                search={search}
                name='some-filter'
                title='Some filter'
            />)

        })
        describe('when filter no currently checked', () => {
            it('call addToFilter with code', () => {
                filter.find({value: 'N02'}).simulate('change', {target: {value: 'N02'}});
                expect(addToFilter).to.be.calledWith('N02', 'N02 Area');
            })
        })
        describe('when filter is currently checked', () => {
            it('call removeFromFilter with code', () => {
                filter.find({value: 'N01'}).simulate('change', {target: {value: 'N01'}});
                expect(removeFromFilter).to.be.calledWith('N01');
            })
        })
    })
    describe('componentWillReceiveProps', () => {
        let filter;
        const currentFilter = ['N01', 'N03'];
        beforeEach(() => {
            filter = shallow(<Filter
                searchTerm='Mr Bean'
                filterValues={[{code: 'N01', description: 'Some Area', count: 67}, {code: 'N02', description: 'Some Other Area', count: 4}, {code: 'N03', description: 'Some Other Area', count: 4}]}
                currentFilter={currentFilter}
                addToFilter={addToFilter}
                removeFromFilter={removeFromFilter}
                search={search}
                name='some-filter'
                title='Some filter'
            />)

        })
        context('when area filter changes', () => {
            it('calls search', () => {
                filter.setProps({ currentFilter: ['N01'] });
                expect(search.calledOnce).to.equal(true);
                expect(search.getCall(0).args[0]).to.equal('Mr Bean');
                expect(search.getCall(0).args[1]).to.eql(['N01']);
            })
        })
        context('when some thing else changes', () => {
            it('calls nothing', () => {
                filter.setProps({ currentFilter: currentFilter });
                expect(search.calledOnce).to.equal(false);
            })

        })
    })
})


