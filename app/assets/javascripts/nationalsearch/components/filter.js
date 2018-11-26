import React from 'react'
import PropTypes from "prop-types";
import {Component} from 'react'

class Filter extends Component {
    constructor(props) {
        super(props);
        this.state = {
            expanded: true
        }
    }
    componentWillReceiveProps(nextProps) {
        const {search, searchTerm, currentFilter} = this.props

        if (!equal(currentFilter, nextProps.currentFilter)) {
            search(searchTerm, nextProps.searchType, nextProps.currentFilter)
        }
    }
    toggleExpanded() {
        this.setState({expanded: !this.state.expanded})
    }
    render() {
        const {filterValues, currentFilter, addToFilter, removeFromFilter, title, name} = this.props
        const {expanded} = this.state
        const toggleFilter = (code, description) => {
            if (currentFilter.indexOf(code) > -1) {
                removeFromFilter(code)
            } else {
                addToFilter(code, description)
            }
        }

        const isPresentInThisFilter = code => filterValues.filter(filter => filter.code === code).length === 1
        const countSelected = () => currentFilter.filter(code =>  isPresentInThisFilter(code)).length

        return (
            <div className='js-stick-at-top-when-scrolling'>
                {shouldDisplayFilter(filterValues) &&
                <div  className='filter'>
                    <button onClick={() => this.toggleExpanded()} type="button" aria-expanded={expanded} aria-controls={`filters-${name}`} className={expanded ? 'open' : 'closed'}>
                        <div id='provider-select-label' className='bold-small'>
                            {title}
                        </div>
                        <div id='selected' className='font-xsmall'>{countSelected()} selected</div>
                    </button>
                    <div role='group'aria-labelledby='provider-select-label' id={`filters-${name}`} className={expanded ? 'open filter-container' : 'closed filter-container'}>
                        <div>
                        {filterValues.map(filterValue => (
                            <label key={filterValue.code} htmlFor={filterValue.code} className='font-xsmall'>
                                <input className='filter-option' tabIndex={1} type='checkbox' value={filterValue.code} id={filterValue.code}
                                       checked={currentFilter.indexOf(filterValue.code) > -1}
                                       onChange={() => toggleFilter(filterValue.code, filterValue.description)}
                                       aria-controls='live-offender-results'
                                />
                                <span>{filterValue.description} ({filterValue.count})</span>
                            </label>
                        ))}
                        </div>
                    </div>
                </div>
                }
            </div>
        )
    }
}

Filter.propTypes = {
    searchTerm: PropTypes.string.isRequired,
    filterValues: PropTypes.arrayOf(PropTypes.shape({
        code: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        count: PropTypes.number.isRequired
    })).isRequired,
    currentFilter: PropTypes.arrayOf(PropTypes.string).isRequired,
    title: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    addToFilter: PropTypes.func.isRequired,
    removeFromFilter: PropTypes.func.isRequired,
    search: PropTypes.func.isRequired
};

const shouldDisplayFilter = filterValues => filterValues.length > 0
const equal = (left, right) => hasAllElements(left, right) && hasAllElements(right, left)
const hasAllElements = (left, right) => left.filter(element => right.indexOf(element) > -1).length === left.length

export default Filter;