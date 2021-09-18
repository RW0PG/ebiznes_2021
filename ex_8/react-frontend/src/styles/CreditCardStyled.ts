import styled from 'styled-components';

export const CreditCardModalStyled = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 30vw;
  height: 70%;
  background-color: #4054b4;
  box-shadow: 10px 10px 5px grey;
  border-radius: 14px 14px 14px 14px;
  padding: 20px 20px 20px 20px;
  
  .cancel {
    display: flex;
    align-self: flex-end;
  }
  
  .content {
    display: flex;
    padding-top: 0.1vh;
    align-items: center;
    justify-content: center;
    flex-direction: column;
  }
  
  .inputFieldRow {
    display: flex;
    flex-direction: column;
    padding-top: 1vh;
  }
  
  input::placeholder {
    text-indent: 10px;
    font-size: small;
  }
  
  .field{
    background-color: #495057;
    border-radius: 14px 14px 14px 14px;
  }
  
  .saveChangesBtn{
    display: flex;
    justify-content: flex-end;
    padding-top: 1vh;
  }
  
  .row{
    align-content: center;
    padding: 10px;
  }

`;
