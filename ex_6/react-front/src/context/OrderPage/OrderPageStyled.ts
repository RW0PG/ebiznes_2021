import styled from 'styled-components';

export const OrderPageStyled = styled.div`
	display: flex;
	align-items: center;
	justify-content: center;

	.entries {
        border-radius: 4px;
        color: black;
		min-width: 30vw;
		padding: 10px;
	}
  
	.order-summary-item {
		min-width: 10vw;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: space-between;
        color: black
	}
  
`;
