import styled from 'styled-components';

export const OrderHistoryPageStyled = styled.div`
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
	}
`;

export const Entry = styled.div`
	margin-top: 40px;
	min-width: 80vw;
	padding: 10px;
	border-radius: 4px;
`
