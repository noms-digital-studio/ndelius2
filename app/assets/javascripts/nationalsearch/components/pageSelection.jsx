export default ({pageSize, page, total}) => (
    <div>
        {shouldDisplay(pageSize, total) &&
        <span>
            <a className='clickable margin-right'>&lt; Previous</a>
            <span className='margin-right'>-</span>
            <span className='margin-right'><a className='clickable'>1</a></span>
            <span className='margin-right'>2</span>
            <span className='margin-right'><a className='clickable'>3</a></span>
            <span className="margin-right">-</span>
            <a className='clickable'>Next &gt;</a>
        </span>
        }
    </div>
);

const shouldDisplay = (pageSize, total) => total > pageSize
