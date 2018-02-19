import PropTypes from 'prop-types'

const AnalyticsCount = ({description, count, fetching}) => (
    <div style={{float: 'left', margin: '10px', backgroundColor: '#f8f8f8', padding: '10px', minWidth: '120px', minHeight: '140px'}}>
        <p style={{fontSize: '16px', textAlign: 'center', margin: '10px'}}>{description}</p>

        <p style={{fontSize: '56px', textAlign: 'center', margin: '10px'}}>
            {fetching && <span>-</span>}
            {!fetching && <span>{count}</span>}
        </p>
    </div>
);


AnalyticsCount.propTypes = {
    count: PropTypes.number.isRequired,
    description: PropTypes.string.isRequired,
    fetching: PropTypes.bool.isRequired
};


export default AnalyticsCount;
