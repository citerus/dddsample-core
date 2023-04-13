import Modal from "./Modal";

export default function ErrorDialog({
  error,
  onClose,
}: {
  error: { error: string; message: string };
  onClose: () => void;
}) {
  function closeModalHandler() {
    onClose();
  }
  return (
    <>
      <Modal onClose={closeModalHandler}>
        <h1>{error.error}</h1>
        <div>{error.message}</div>
      </Modal>
    </>
  );
}
