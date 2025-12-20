import logging

def debug_here(message):
    logging.debug(f"[DEBUG] {message}")
    breakpoint()  # You can set a breakpoint here for debugging purposes
