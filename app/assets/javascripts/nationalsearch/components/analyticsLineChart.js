import React, { Component } from 'react'
import PropTypes from 'prop-types'

class AnalyticsLineChart extends Component {
  render () {
    const { description } = this.props
    return (
      <div style={{
        float: 'left',
        margin: '10px',
        backgroundColor: '#f8f8f8',
        padding: '10px',
        minWidth: '440px',
        minHeight: '300px'
      }}>
        <p style={{ fontSize: '16px', textAlign: 'center', margin: '10px' }}>{description}</p>

        <canvas ref={(canvas) => { this.canvas = canvas }} />
      </div>
    )
  }

  componentDidUpdate () {
    if (this.chart) {
      this.chart.destroy()
    }

    this.chart = new Chart(this.canvas.getContext('2d'), chartOptions(this.props))
  }
}

export const chartOptions = props => {
  const { label, numberToCountData, xAxesLabel } = props
  const defaultLabelMapper = data => Object.getOwnPropertyNames(data)
  const labelMapper = props.labelMapper || defaultLabelMapper

  return {
    type: 'line',
    data: {
      labels: labelMapper(numberToCountData),
      datasets: [{
        label,
        data: Object.getOwnPropertyNames(numberToCountData).map(property => numberToCountData[property]),
        backgroundColor: '#2b8cc4',
        borderWidth: 1,
        fill: false
      }]
    },
    options: {
      scales: {
        yAxes: [{
          ticks: {
            beginAtZero: true
          }
        }],
        xAxes: [{
          scaleLabel: {
            display: true,
            labelString: xAxesLabel
          }
        }]
      }
    }
  }
}

AnalyticsLineChart.propTypes = {
  description: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  xAxesLabel: PropTypes.string.isRequired,
  fetching: PropTypes.bool.isRequired,
  numberToCountData: PropTypes.object.isRequired,
  labelMapper: PropTypes.func
}

export default AnalyticsLineChart
