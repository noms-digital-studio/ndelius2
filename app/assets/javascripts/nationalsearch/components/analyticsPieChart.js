import React from 'react'
import PropTypes from 'prop-types'
import {Component} from 'react'

class AnalyticsPieChart extends Component {
    constructor(props) {
        super(props);
    }
    render() {
        const {description} = this.props
        return (
            <div style={{float: 'left', margin: '10px', backgroundColor: '#f8f8f8', padding: '10px', minWidth: '440px', minHeight: '400px'}}>
                <p style={{fontSize: '16px', textAlign: 'center', margin: '10px'}}>{description}</p>

                <canvas ref={(canvas) => { this.canvas = canvas; }}/>
            </div>
        )
    }
    componentDidUpdate() {
        if (this.chart) {
            this.chart.destroy()
        }

        this.chart = new Chart(this.canvas.getContext('2d'), chartOptions(this.props));
    }
}


export const chartOptions = props => {
    const {label, numberToCountData} = props
    const defaultLabelMapper = data => Object.getOwnPropertyNames(data)
    const labelMapper = props.labelMapper || defaultLabelMapper

    return {
        type: 'pie',
        data: {
            labels: labelMapper(numberToCountData),
            datasets: [{
                label,
                data: Object.getOwnPropertyNames(numberToCountData).map(property => numberToCountData[property]),
                backgroundColor: [
                    '#2e358b',
                    '#912b88',
                    '#d53880',
                    '#f47738',
                    '#b58840',
                    '#ffbf47',
                    '#85994b',
                    '#28a197',
                    '#2b8cc4',
                    '#9799c4',
                    '#faccdf',
                    '#d9888c',
                    '#ef9998',
                    '#fabb96',
                    '#dac39c',
                    '#ffdf94',
                    '#c2cca3',
                    '#7fb299',
                    '#95d0cb',
                    '#96c6e2'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: false

        }
    }

}


AnalyticsPieChart.propTypes = {
    description: PropTypes.string.isRequired,
    label: PropTypes.string.isRequired,
    fetching: PropTypes.bool.isRequired,
    numberToCountData: PropTypes.object.isRequired,
    labelMapper: PropTypes.func
};


export default AnalyticsPieChart;
