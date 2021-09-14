import styled from 'styled-components';

export const CartStyled = styled.div`
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	.entries {
		padding: 10px;
        background-color: rgb(255, 255, 255);
		border-radius: 4px;
	}

	.head {
		padding: 5px;
		background-color: rgb(255, 255, 255);
		display: flex;
		flex-direction: row;
		border-radius: 1px;
		margin-bottom: 10px;
	}

	.section {
		min-width: 15vw;
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 0;
	}
	
	.summary {
		padding: 10px;
		text-align: right;
		margin-right: 5vw;
	}
`;
